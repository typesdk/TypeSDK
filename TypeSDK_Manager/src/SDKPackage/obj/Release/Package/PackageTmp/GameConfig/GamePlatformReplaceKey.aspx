<%@ Page Title="编译替换参数配置" Language="C#" ValidateRequest="false" MasterPageFile="~/Admin.Master" AutoEventWireup="true" CodeBehind="GamePlatformReplaceKey.aspx.cs" Inherits="SDKPackage.GameConfig.GamePlatformReplaceKey" %>

<asp:Content ID="Content1" ContentPlaceHolderID="MainContent" runat="server">
    <!-- PNotify -->
    <link href="../vendors/pnotify/dist/pnotify.css" rel="stylesheet">
    <link href="../vendors/pnotify/dist/pnotify.buttons.css" rel="stylesheet">
    <link href="../vendors/pnotify/dist/pnotify.nonblock.css" rel="stylesheet">
    <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
            <div class="x_panel">
                <div class="x_title">
                    <h2>配置生成replace.xml</h2>
					<ul class="nav navbar-right panel_toolbox">
                      <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
                      </li>
                    </ul>
				<div class="clearfix"></div>
                </div>
                <div class="x_content">
                    <div>

                        <div>
                            <span class="name">游戏:</span>
                            <span class="value text-primary"><%= gamename %></span>
                        </div>
                        <div>
                            <span class="name">平台:</span>
                            <span class="value text-primary"><%= platformname %></span>
                        </div>
                        <div>
                            <asp:Label ID="LabelMessage" runat="server" class="hide" Text=""></asp:Label>
                        </div>
                        <asp:CheckBoxList ID="CheckBoxListIsBinding" runat="server" RepeatDirection="Horizontal"></asp:CheckBoxList>
                    </div>
					<div class="row">
                    <div class="col-md-12 col-sm-12 col-xs-12">
                        <asp:TextBox ID="TextBoxNodeIndex" runat="server" CssClass="form-control" Style="width: 80px; float: left; margin-right: 10px;" placeholder="节点" Text="1"></asp:TextBox>
                        <asp:Button ID="ButtonAddReplacefilter" runat="server" Text="添加replacefilter" CssClass="btn btn-primary" OnClick="ButtonAddReplacefilter_Click" />
                    </div>
					</div>
					<div class="row">
                    <div class="col-md-12 col-sm-12 col-xs-12">
                        <asp:TextBox ID="TextBoxDir" runat="server" CssClass="form-control" Style="width: 200px; float: left; margin-right: 10px;" placeholder="文件路径"></asp:TextBox>
                        <asp:TextBox ID="TextBoxIncludes" runat="server" CssClass="form-control" Style="width: 200px; float: left; margin-right: 10px;" placeholder="文件名"></asp:TextBox>
                        <asp:Button ID="ButtonAddNdoe" runat="server" Text="添加节点" CssClass="btn btn-primary" OnClick="ButtonAddNdoe_Click" />
				<div class="clearfix"></div>
                    </div>
                    </div>
                    <div>
                        <asp:TextBox ID="TextBoxContext" runat="server" TextMode="MultiLine" CssClass="form-control" Style="height: 400px"></asp:TextBox>
                    </div>
                    <hr />
                    <!--保存配置文件-->
                    <%--<a class="btn btn-primary btn-lg" style="" href='AddGamePlatfrom.aspx?gameid=<%=gameid %>&androidversionid=<%=androidversionid %>&iosversionid=<%=iosversionid %>&gamedisplayname=<%=gamedisplayname %>'>返回</a>--%>
                    <a class="btn btn-primary" data-toggle="modal" href="#createConfigFile"><i class='fa fa-save'></i> 保存至项目配置文件</a>
                    <div class="modal fade" id="createConfigFile" tabindex="-1" role="dialog" aria-labelledby="createConfigFileLabel" aria-hidden="true">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                                        ×
                                    </button>
                                    <h4 class="modal-title" id="createConfigFileLabel">确认保存操作
                                    </h4>
                                </div>
                                <div class="modal-body">
                                    保存操作将覆盖项目内现有配置文件，是否确认需要覆盖保存。
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-default" data-dismiss="modal">
                                        取消
                                    </button>
                                    <asp:Button ID="ButtonCreateConfigFile" runat="server" class="btn btn-primary" OnClick="ButtonCreateConfigFile_Click" Text="确认保存" />
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- PNotify -->
    <script src="/vendors/pnotify/dist/pnotify.js"></script>
    <script src="/vendors/pnotify/dist/pnotify.buttons.js"></script>
    <script src="/vendors/pnotify/dist/pnotify.nonblock.js"></script>
 <script type="text/javascript">
        $(document).ready(function() {
			if($('#MainContent_LabelMessage').text()){
			new PNotify({
                                  title: '提示',
                                  text: $('#MainContent_LabelMessage').text(),
                                  type: 'info',
                                  styling: 'bootstrap3',
                                  addclass: 'dark'
                              });
			}
		});
    </script>
</asp:Content>
