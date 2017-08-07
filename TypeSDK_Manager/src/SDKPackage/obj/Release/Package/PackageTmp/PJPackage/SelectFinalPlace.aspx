<%@ Page Title="SDK打包管理系统" Language="C#" MasterPageFile="~/Admin.Master" AutoEventWireup="true" CodeBehind="SelectFinalPlace.aspx.cs" Inherits="SDKPackage.PJPackage.SelectFinalPlace" %>
<asp:Content ID="BodyContent" ContentPlaceHolderID="MainContent" runat="server">
<style>
.form-group .text-danger label{
    padding-left: 5px;
    margin-bottom: 0px;
    line-height: 24px;
}
.form-group span{
    line-height: 25px;
}
</style>
    <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
            <div class="x_panel">
                <div class="x_title">
                    <h2>确认打包任务</h2>
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
                          <a href="#step-3" class="done" isdone="1" rel="3">
                            <span class="step_no">3</span>
                            <span class="step_descr">
                                              <small>选择项目</small>
                                          </span>
                          </a>
                        </li>
                        <li>
                          <a href="#step-4" class="done" isdone="0" rel="4">
                            <span class="step_no">4</span>
                            <span class="step_descr">
                                              <small>选择渠道</small>
                                          </span>
                          </a>
                        </li>
                        <li>
                          <a href="#step-5" class="selected" isdone="0" rel="5">
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
                        <div class="form-group">
                            <span class="name">版本:</span>
                            <span class="value text-primary"><%= gameversion %></span>
                        </div>
                    </div>
                    <div class="form-inline navbar-right">
                        <div class="form-group">
                            <span class="name text-danger">选项:</span>
                        </div>
                        <div class="form-group">
                            <asp:CheckBox ID="CheckBoxIsEncryption" runat="server" CssClass="value text-danger" />
                        </div>
                        <div class="form-group">
                            <asp:CheckBox ID="CheckBoxCompileMode" runat="server" Visible="False" CssClass="value text-danger"/>
                        </div>
                    </div>

                    <table class="table table-striped jambo_table" id="MainContent_GamePlaceList_itemPlaceholderContainer">
					<thead>
                        <tr>
                            <th>渠道名称</th>
                            <th>渠道编号</th>
                            <th>渠道版本</th>
                        </tr>
					</thead>
                        <asp:Repeater ID="GamePlaceList" runat="server" DataSourceID="SqlDataSourceGamePlaceList">

                            <ItemTemplate>
                                <tr>
                                    <td><%#Eval("PlatformDisplayName")%></td>
                                    <td><%#Eval("PlatformName")%></td>
                                    <td><%#Eval("Version") %>
                                        <asp:HiddenField ID="HiddenFieldPlatformID" runat="server" Value='<%#Eval("ID").ToString()+"_"+Eval("PlugInID").ToString() %>' />
                                        <asp:HiddenField ID="HiddenFieldAdID" runat="server" />
                                    </td>
                                </tr>
                            </ItemTemplate>
                        </asp:Repeater>
                    </table>


                    <asp:SqlDataSource ID="SqlDataSourceGamePlaceList" runat="server" ConnectionString="<%$ ConnectionStrings:SdkPackageConnString %>" SelectCommand="[sdk_getGameFinalPlatforms]" SelectCommandType="StoredProcedure">
                        <SelectParameters>
                            <asp:QueryStringParameter Name="GameID" Type="Int32" QueryStringField="gameid" />
                            <asp:QueryStringParameter Name="SystemID" Type="String" QueryStringField="platform" />
                            <asp:QueryStringParameter Name="PlaceIDList" Type="String" QueryStringField="placeidlist" />
                        </SelectParameters>
                    </asp:SqlDataSource>
                    <br />
                    <br />
                    <div class="text-center">
                        <input type="hidden" id="savegamedisplayname" value="<%= gameDisplayName %>" />
                        <input type="hidden" id="savegamename" value="<%= gameName %>" />
                        <input type="hidden" id="savegameid" value="<%= gameId %>" />
                        <input type="hidden" id="savegamenamespell" value="<%= gamenamespell %>" />
                        <input type="hidden" id="saveplatform" value="<%= platform %>" />
                        <input type="hidden" id="savetaskid" value="<%= taskid %>" />
                        <input type="hidden" id="savegameversion" value="<%= gameversion %>" />
                        <input type="hidden" id="savegamelable" value="<%= gamelable %>" />
                        <input type="hidden" id="saveplaceidlist" value="<%= placeidlist %>" />
                        <input id="btnPre" type="button" name="buttonPre" value=" 上一步 " onclick="back()" class="btn btn-primary">&nbsp&nbsp
        <asp:Button ID="btnStart" runat="server" Text="  开始打包  " CssClass="btn btn-success" OnClick="btnStart_Click" />
                        <%--<input id="btnNext" type="button" name="buttonNext" value="  开始打包  " onclick="NextStep()" class="btn btn-success">--%>
                        <input id="hfreturnVal" type="hidden" />
                        <%--<asp:HiddenField ID="hfPlaceidList" runat="server" />--%>
                        <asp:Label ID="LabelLog" runat="server" Text=""></asp:Label>
                        <asp:HiddenField ID="hfCreateTaskId" runat="server" />
                    </div>
                    </div>
                </div>
            </div>
        </div>
    </div>


    <script type="text/javascript">
        function back() {
            var selgameid = "";
            var selgamename = "";
            var selgamedisplayname = "";
            var selgamenamespel = "";
            var selplatform = "";
            var seltaskid = "";
            var selgameversion = "";
            var selgamelable = "";
            var selplaceidlist = "";
            selgamename = document.getElementById('savegamename').value; //游戏简称
            selgamedisplayname = document.getElementById('savegamedisplayname').value; //游戏名称
            selgameid = document.getElementById('savegameid').value;     //游戏ID
            selgamenamespell = document.getElementById('savegamenamespell').value;     //游戏全拼
            selplatform = document.getElementById('saveplatform').value; //平台
            seltaskid = document.getElementById('savetaskid').value; //数据ID
            selgameversion = document.getElementById('savegameversion').value; //版本
            selgamelable = document.getElementById('savegamelable').value; //版本_标签
            selplaceidlist = document.getElementById('saveplaceidlist').value;
            var urlParam = "?gameid=" + selgameid + "&gamename=" + selgamename + "&gamedisplayname=" + selgamedisplayname + "&gamenamespell=" + selgamenamespell + "&platform=" + selplatform + "&taskid=" + seltaskid + "&gameversion=" + selgameversion + "&gamelable=" + selgamelable;

            //if (selplaceidlist != "") {
            urlParam = urlParam + "&placeidlist=" + selplaceidlist;
            //}
            window.location.href = "./SelectPlace.aspx" + urlParam;
        }

        var idx = 0;
        function recordrow(obj) {
            idx = $(obj).closest('tr').index();
            var v = $("#MainContent_GamePlaceList_itemPlaceholderContainer tr").eq(idx).find("input[type='hidden']").eq(1).val();
            //alert($("#MainContent_GamePlaceList_itemPlaceholderContainer tr").eq(idx).find("input[type='hidden']").eq(0).val());
            InsertCkBox(v);
        }

        $(function () {
            $("#MainContent_ButtonSave").click(function () {

                var channel_list = $("#MainContent_CheckBoxListChannel input:checked");
                var hidden = $("#MainContent_GamePlaceList_itemPlaceholderContainer tr").eq(idx).find("input[type='hidden']").eq(1);
                var v = "";
                for (var i = 0; i < channel_list.length; i++) {
                    v += $(channel_list[i]).val() + ",";
                }
                v = v.substring(0, v.length - 1);
                $(hidden).val(v);
            });

        })

        function InsertCkBox(v) {
            if (v.length > 0) {
                var v_list = v.split(',');
                var channel_list = $("#MainContent_CheckBoxListChannel input:checkbox");
                for (var i = 0; i < channel_list.length; i++) {
                    for (var j = 0; j < v_list.length; j++) {
                        var channelid = $(channel_list[i]).val();
                        //alert(channelid);
                        if (channelid == v_list[j]) {
                            //alert(channelid);
                            //$(channel_list[i]).attr("checked", false);
                            $(channel_list[i]).prop("checked", true);
                            break;
                        }
                        if (j + 1 == v_list.length) {
                            $(channel_list[i]).prop("checked", false);
                        }
                    }
                }
            } else {
                $("#MainContent_CheckBoxListChannel input:checkbox").prop("checked", false);
            }
        }
    </script>

</asp:Content>
