<%@ Page Title="游戏版本管理" Language="C#" MasterPageFile="~/Admin.Master" AutoEventWireup="true" CodeBehind="GameProjectVersionManage.aspx.cs" Inherits="SDKPackage.PJPackage.GameProjectVersionManage" %>

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
                    <div class="form-inline navbar-left">
                        <div class="form-group">
                            <span class="name">游戏:</span>
                            <%--<span class="value text-primary"><%= gameDisplayName %></span>--%>
                            <asp:DropDownList ID="ddlGames" CssClass="form-control" runat="server" DataSourceID="dsGameNameList" DataTextField="GameDisplayName" DataValueField="GameID" AutoPostBack="True" OnSelectedIndexChanged="ddlGames_SelectedIndexChanged">
                            </asp:DropDownList>
                            <asp:SqlDataSource ID="dsGameNameList" runat="server" ConnectionString="<%$ ConnectionStrings:SdkPackageConnString %>" SelectCommand="sdk_getGameList" SelectCommandType="StoredProcedure">
                                <SelectParameters>
                                    <asp:ControlParameter ControlID="saveusername" Type="String" Name="UserName" />
                                </SelectParameters>
                            </asp:SqlDataSource>
                        </div>
                        <div class="form-group">
                            <span class="name">平台:</span>
                            <%--<span class="value text-primary"><%= platform %></span>--%>
                            <asp:DropDownList ID="ddlPlatforms" runat="server" CssClass="form-control" AutoPostBack="True" OnSelectedIndexChanged="ddlPlatforms_SelectedIndexChanged">
                                <asp:ListItem Value="Android">Android</asp:ListItem>
                                <asp:ListItem Value="IOS">IOS</asp:ListItem>
                            </asp:DropDownList>
                        </div>
                    </div>
                    <div class="navbar-right">
                        <a class="btn btn-primary btn-sm" onclick="openfile()">上传新项目文件</a>
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
                                    <td><a class="btn btn-xs btn-danger" onclick='deleteGamePackage(this,<%#"\""+Eval("ID").ToString()+"\"" %>,<%#"\""+Eval("GameVersion").ToString()+"\""%>,<%#"\""+Eval("StrCollectDatetime").ToString()+"\""%>)'><i class="fa fa-trash"></i> 删除</a></td>
                                    <td class='tdReview' data-id='<%#Eval("ID") %>'>
                                        <%# Eval("Status").Equals(0) ? "<a class=\"btn btn-xs btn-success\" onclick='reviewGameProjectVersion(\"" + Eval("ID").ToString() + "\",1)'><i class=\"fa fa-check\"></i>通过</a><a class=\"btn btn-xs btn-danger\" onclick='reviewGameProjectVersion(\"" + Eval("ID").ToString() + "\",2)'><i class=\"fa fa-reply\"></i>打回</a>"
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
                                            <th>删除文件</th>
                                            <th>审核操作</th>
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
  [sdk_UploadPackageInfo] upi inner join AspNetUsers us on upi.UploadUser=us.UserName and GameID=@GameID and GamePlatFrom=@SystemName order by upi.id desc">
                        <SelectParameters>
                            <%--<asp:QueryStringParameter QueryStringField="gameid" Type="Int32" Name="GameID" />--%>
                            <asp:ControlParameter ControlID="ddlGames" Name="GameID" PropertyName="SelectedValue"  Type="Int32"/>
                            <%--<asp:QueryStringParameter QueryStringField="platform" Type="String" Name="SystemName" />--%>
                            <asp:ControlParameter ControlID="ddlPlatforms" Name="SystemName" PropertyName="SelectedValue"  Type="String" />
                        </SelectParameters>
                    </asp:SqlDataSource>
                </div>
            </div>
        </div>
        <asp:HiddenField ID="saveusername" runat="server" />

        <input type="hidden" id="savegamedisplayname" value="<%= gameDisplayName %>" />
        <input type="hidden" id="savegamename" value="<%= gameName %>" />
        <input type="hidden" id="savegameid" value="<%= gameId %>" />
        <input type="hidden" id="savegamenamespell" value="<%= gamenamespell %>" />

        <input type="hidden" id="saveplatform" value="<%= platform %>" />
    </div>
    
    <script type="text/javascript">

      <%--  <% if (isBack)
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
        <%}%>--%>

        var timer;
        var winOpen;
        function openfile() {
            $("#hfreturnVal").val("");
            var selgamename = document.getElementById('savegamename').value; //游戏名称
            var selgameid = document.getElementById('savegameid').value;     //游戏ID
            var selplatform = document.getElementById('saveplatform').value; //平台
            var selgamenamespell = document.getElementById('savegamenamespell').value;     //游戏全拼
            var str_href = "../PJPackage/GameVersionAdd.aspx?gameid=" + selgameid + "&gamename=" + selgamename + "&gamenamespell=" + selgamenamespell + "&platform=" + selplatform;
            winOpen = window.open(str_href, '上传文件', 'height=320,width=800,top=20%,left=30%,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no');
            timer = window.setInterval("IfWindowClosed()", 500);
        }

        function IfWindowClosed() {
            if (winOpen.closed == true) {
                var hf_val = $("#hfreturnVal").val();
                //alert("子页面关闭=>返回值：" + hf_val);
                window.clearInterval(timer);
                if (hf_val == "success")
                    window.location.reload();
            }
        }

        function reviewGameProjectVersion(id, status) {
            if (confirm("确定要审核数据吗？")) {
                $.ajax({
                    contentType: "application/json",
                    async: false,
                    url: "/WS/WSNativeWeb.asmx/ReviewGameProjectVersion",
                    data: "{id:" + id + ",status:" + status + "}",
                    type: "POST",
                    dataType: "json", success: function (json) {
                        json = eval("(" + json.d + ")");

                        if (json.ret === 0) {
                            $(".tdReview[data-id='" + id + "']").text((status == 1) ? '已通过' : '已打回');
                        } else {
                            alert('ERR');
                        }
                    }
                });
            }
        }

        function deleteGamePackage(obj, id, version, strdatetime) {
            if (confirm("确定要删除数据吗？")) {
                var tr = $(obj).parent("td").parent("tr");
                var platform = "<%=platform%>";
                var filepath = "";
                var gamename = "";
                var gamename1 = "<%=gameName%>";
                var gameid = "<%=gameId%>";
                var gamename2 = "<%=gamenamespell%>";
                if (platform == "Android") {
                    filepath = version + "_" + strdatetime;
                    gamename = gamename1;
                }
                else {
                    filepath = strdatetime;
                    gamename = gamename2;
                }
                $.ajax({
                    contentType: "application/json",
                    async: false,
                    url: "/WS/WSNativeWeb.asmx/DeleteGamePackage",
                    data: "{id:'" + id + "',platform:'" + platform + "',gameId:'" + gameid + "',filepath:'" + filepath + "'}",
                    type: "POST",
                    dataType: "json", success: function () {
                        tr.hide();
                    }
                });
            }
        }
    </script>
</asp:Content>


