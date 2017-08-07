<%@ Page Title="SDK打包管理系统" Language="C#" MasterPageFile="~/Admin.Master" AutoEventWireup="true" CodeBehind="SelectGame.aspx.cs" Inherits="SDKPackage.PJPackage.SelectGame" %>

<asp:Content ID="BodyContent" ContentPlaceHolderID="MainContent" runat="server">
    <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
            <div class="x_panel">
                <div class="x_title">
                    <h2>游戏选择</h2>
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
                                <a href="#step-1" class="selected" isdone="1" rel="1">
                                    <span class="step_no">1</span>
                                    <span class="step_descr">
                                        <small>选择游戏</small>
                                    </span>
                                </a>
                            </li>
                            <li>
                                <a href="#step-2" class="disabled" isdone="1" rel="2">
                                    <span class="step_no">2</span>
                                    <span class="step_descr">
                                        <small>选择平台</small>
                                    </span>
                                </a>
                            </li>
                            <li>
                                <a href="#step-3" class="disabled" isdone="1" rel="3">
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
                        <hr />
                        <div class="row">
                            <asp:Repeater ID="Repeater1" runat="server" DataSourceID="GameNameList">
                                <ItemTemplate>
                                    <div class="col-md-55">
                                        <a onclick="NextStep(<%# Eval("GameId") %>);">
                                            <div class="thumbnail">
                                                <img src="<%# Eval("GameIcon") %>" alt="image">
                                                <div class="text-center">
                                                    <p><%# Eval("GameDisplayName") %></p>
                                                </div>
                                            </div>
                                        </a>
                                        <input type="hidden" id="gamedisplayname<%# Eval("GameId") %>" value="<%# Eval("GameDisplayName") %>" />
                                        <input type="hidden" id="gamename<%# Eval("GameId") %>" value="<%# Eval("GameName") %>" />
                                        <input type="hidden" id="selgamenamespell<%# Eval("GameId") %>" value="<%# Eval("GameNameSpell") %>" />
                                    </div>
                                </ItemTemplate>
                            </asp:Repeater>
                            <asp:SqlDataSource ID="GameNameList" runat="server" ConnectionString="<%$ ConnectionStrings:SdkPackageConnString %>" SelectCommand="sdk_getGameList" SelectCommandType="StoredProcedure">
                                <SelectParameters>
                                    <asp:ControlParameter ControlID="saveusername" Type="String" Name="UserName" />
                                </SelectParameters>
                            </asp:SqlDataSource>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <asp:HiddenField ID="saveusername" runat="server" />
    </div>
    <script type="text/javascript">
        function NextStep(gameid) {
            var selgameid = gameid;
            var selgamename = ""
            var selgamedisplayname = "";
            var selgamenamespell = ""
            try {
                var id = 'gamedisplayname' + selgameid;
                var id2 = 'gamename' + selgameid;
                var id3 = 'selgamenamespell' + selgameid;
                selgamename = document.getElementById(id2).value; //游戏简称
                selgamedisplayname = document.getElementById(id).value; //游戏名称
                selgamenamespell = document.getElementById(id3).value; //游戏全拼
                var urlParam = "?gameid=" + selgameid + "&gamename=" + selgamename + "&gamedisplayname=" + selgamedisplayname + "&gamenamespell=" + selgamenamespell;

                window.location.href = "./SelectPlatform.aspx" + urlParam;
            } catch (e) {
                alert("页面跳转出错.\n错误信息：" + e.message);
            }
        }

    </script>
</asp:Content>

