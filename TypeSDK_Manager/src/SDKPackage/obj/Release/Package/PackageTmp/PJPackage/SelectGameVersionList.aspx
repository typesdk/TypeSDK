<%@ Page Title="SDK打包管理系统" Language="C#" MasterPageFile="~/Admin.Master" AutoEventWireup="true" CodeBehind="SelectGameVersionList.aspx.cs" Inherits="SDKPackage.PJPackage.SelectGameVersionList" %>

<asp:Content ID="BodyContent" ContentPlaceHolderID="MainContent" runat="server">
    <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
            <div class="x_panel">
                <div class="x_title">
                    <h2>选择项目文件</h2>
					<ul class="nav navbar-right panel_toolbox">
                      <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
                      </li>
                    </ul>
				<div class="clearfix"></div>
                </div>
                <div class="x_content">
                    <div id="wizard" class="form_wizard wizard_horizontal">
					<ul class="wizard_steps anchor">
                        <li>
                          <a href="#step-1" class="done" isdone="1" rel="1">
                            <span class="step_no">1</span>
                            <span class="step_descr">
                                              <small>选择游戏</small>
                                          </span>
                          </a>
                        </li>
                        <li>
                          <a href="#step-2" class="done" isdone="1" rel="2">
                            <span class="step_no">2</span>
                            <span class="step_descr">
                                              <small>选择平台</small>
                                          </span>
                          </a>
                        </li>
                        <li>
                          <a href="#step-3" class="selected" isdone="1" rel="3">
                            <span class="step_no">3</span>
                            <span class="step_descr">
                                              <small>选择项目</small>
                                          </span>
                          </a>
                        </li>
                        <li>
                          <a href="#step-4" class="disabled" isdone="0" rel="4">
                            <span class="step_no">4</span>
                            <span class="step_descr">
                                              <small>选择渠道</small>
                                          </span>
                          </a>
                        </li>
                        <li>
                          <a href="#step-5" class="disabled" isdone="0" rel="5">
                            <span class="step_no">5</span>
                            <span class="step_descr">
                                              <small>确认任务</small>
                                          </span>
                          </a>
                        </li>
                        <li>
                          <a href="#step-6" class="disabled" isdone="0" rel="6">
                            <span class="step_no">6</span>
                            <span class="step_descr">
                                              <small>开始打包</small>
                                          </span>
                          </a>
                        </li>
                      </ul>
                    </div>
                    <hr />
                    <div class="form-inline navbar-left">
                        <div class="form-group">
                            <span class="name">游戏:</span>
                            <span class="value text-primary"><%= gameDisplayName %></span>
                        </div>
                        <div class="form-group">
                            <span class="name">平台:</span>
                            <span class="value text-primary"><%= platform %></span>
                        </div>
                    </div>
                    <div>
                        <asp:ListView ID="GameVersionList" runat="server" DataSourceID="SqlDataSource1" DataKeyNames="ID" OnItemCommand="GameVersionList_ItemCommand">
                            <EmptyDataTemplate>
                                <div class="no-data">
                                    没有发现项目文件
                                </div>
                            </EmptyDataTemplate>

                            <ItemTemplate>
									<div class="form-group">
									<div class="checkbox">
                                <tr>
                                    <td>
                                        <input type="radio" class="flat" name="radiogame" value="<%#Eval("ID") %>" />
						            </td>
                                    <td><%#Eval("GameVersion") %></td>
                                    <td><%#Eval("PageageTable") %></td>
                                    <td><%#Eval("CollectDatetime") %></td>
                                    <td><%#Eval("FileSize") %>M</td>
                                    <td><%#Eval("Compellation") %></td>
                                    <td>
                                        <%# Eval("Status").Equals(0) ? "未审核"
                                                                     : Eval("Status").Equals(1) ? "已通过"
                                                                                                : "已打回"
                                        %>
                                    </td>
                                </tr>
                          </div>
                          </div>
                            </ItemTemplate>

                            <LayoutTemplate>
                                <table id="itemPlaceholderContainer" class="table table-striped jambo_table">
                                    <thead>
                                        <tr>
                                            <th></th>
                                            <th>版本</th>
                                            <th>识别标签</th>
                                            <th>创建时间</th>
                                            <th>大小</th>
                                            <th>所有者</th>
                                            <th>审核状态</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr id="itemPlaceholder" runat="server">
                                        </tr>
                                    </tbody>
                                </table>
                            </LayoutTemplate>
                        </asp:ListView>
                    </div>
                    <asp:SqlDataSource ID="SqlDataSource1" runat="server" ConnectionString="<%$ ConnectionStrings:SdkPackageConnString %>" SelectCommand="select upi.ID,upi.[GameVersion],upi.[PageageTable],upi.[CollectDatetime],upi.FileSize,upi.StrCollectDatetime,us.[Compellation],upi.[Status] from 
  [sdk_UploadPackageInfo] upi inner join AspNetUsers us on upi.UploadUser=us.UserName and GameID=@GameID and GamePlatFrom=@SystemName and (case when @Status = 0 then upi.[Status] else @Status end) = upi.[Status] order by upi.id desc">
                        <SelectParameters>
                            <asp:QueryStringParameter QueryStringField="gameid" Type="Int32" Name="GameID" />
                            <asp:QueryStringParameter QueryStringField="platform" Type="String" Name="SystemName" />
                            <%--<asp:Parameter Name="Status" Type="Int32"  DefaultValue="0" />--%>
                        </SelectParameters>
                    </asp:SqlDataSource>
                    <div class="text-center">
                        <input type="hidden" id="savegamedisplayname" value="<%= gameDisplayName %>" />
                        <input type="hidden" id="savegamename" value="<%= gameName %>" />
                        <input type="hidden" id="savegameid" value="<%= gameId %>" />
                        <input type="hidden" id="savegamenamespell" value="<%= gamenamespell %>" />
                        <input type="hidden" id="saveplatform" value="<%= platform %>" />
                        <input id="btnPre" type="button" name="buttonPre" value=" 上一步 " onclick="back()" class="btn btn-primary">&nbsp&nbsp
                        <input id="btnNext" type="button" name="buttonNext" value="  下一步  " onclick="NextStep()" class="btn btn-primary">
                        <input id="hfreturnVal" type="hidden" />
                    </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script type="text/javascript">

        <% if (isBack)
           { %>
        var obj = document.getElementsByName('radiogame');
        for (i = 0; i < obj.length; i++) {
            if (obj[i].value == '<%= taskid %>') {
                var selectobj = obj[i];
                $(selectobj).prop("checked", true);
            }
        }
        <% }
           else
           { %>
        var obj = document.getElementsByName('radiogame');
        $(obj[0]).prop("checked", true);
        <%}%>

        function NextStep() {
            var taskid = $("input[name='radiogame']:checked").val();
            if (taskid == "" || taskid == undefined || taskid == null) {
                alert("抱歉，没有选择源文件！");
                return;
            }
            var gameversion = $("input[name='radiogame']:checked").parent("div").parent("td").next("td").text();
            var gamelable = $("input[name='radiogame']:checked").parent("div").parent("td").next("td").next("td").text();
            var selgameid = "";
            var selgamename = "";
            var selgamedisplayname = "";
            var selgamenamespell = "";
            var selplatform = "";

            selgamename = document.getElementById('savegamename').value; //游戏简称
            selgamedisplayname = document.getElementById('savegamedisplayname').value; //游戏名称
            selgameid = document.getElementById('savegameid').value;     //游戏ID
            selgamenamespell = document.getElementById('savegamenamespell').value;     //游戏全拼
            selplatform = document.getElementById('saveplatform').value; //平台

            var urlParam = "?gameid=" + selgameid + "&gamename=" + selgamename + "&gamedisplayname=" + selgamedisplayname + "&gamenamespell=" + selgamenamespell + "&platform=" + selplatform;
            var url = window.location.pathname
            urlParam = urlParam + "&taskid=" + taskid + "&gameversion=" + gameversion + "&gamelable=" + gamelable;
            window.location.href = "./SelectPlace.aspx" + urlParam;

        }

        function back() {
            var selgameid = "";
            var selgamename = "";
            var selgamedisplayname = "";
            var selplatform = "";
            var selgamenamespell = "";

            selgamename = document.getElementById('savegamename').value; //游戏简称
            selgamedisplayname = document.getElementById('savegamedisplayname').value; //游戏名称
            selgameid = document.getElementById('savegameid').value;     //游戏ID
            selgamenamespell = document.getElementById('savegamenamespell').value;     //游戏全拼
            var urlParam = "?gameid=" + selgameid + "&gamename=" + selgamename + "&gamedisplayname=" + selgamedisplayname + "&gamenamespell=" + selgamenamespell;
            selplatform = document.getElementById('saveplatform').value; //平台
            if (selplatform != "") {
                urlParam = urlParam + "&platform=" + selplatform;
            }
            window.location.href = "./SelectPlatform.aspx" + urlParam;
        }
        var timer;
        var winOpen;

        function IfWindowClosed() {
            if (winOpen.closed == true) {
                var hf_val = $("#hfreturnVal").val();
                //alert("子页面关闭=>返回值：" + hf_val);
                window.clearInterval(timer);
                if (hf_val == "success")
                    window.location.reload();
            }
        }
        
    </script>
</asp:Content>

