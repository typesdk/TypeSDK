<%@ Page Title="SDK打包管理系统" Language="C#" MasterPageFile="~/Admin.Master" AutoEventWireup="true" CodeBehind="SelectPlatform.aspx.cs" Inherits="SDKPackage.PJPackage.SelectPlatform" %>

<asp:Content ID="BodyContent" ContentPlaceHolderID="MainContent" runat="server">
    <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
            <div class="x_panel">
                <div class="x_title">
                    <h2>选择平台</h2>
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
                          <a href="#step-2" class="selected" isdone="1" rel="2">
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
                    </div>
                    <hr />
                    <div class="row">
                        <div class="below-content-title">
                            <span class="name">游戏:</span>
                            <span class="value text-primary"><%= gameDisplayName %></span>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 text-center">
                            <a class="thumbnail" onclick="SelectItem('Android');">
							<i class="fa fa-android" style="font-size:150px;"></i>
                            </a>
                        </div>
                        <div class="col-md-6 text-center">
                            <a class="thumbnail" onclick="SelectItem('IOS');">
							<i class="fa fa-apple" style="font-size:150px;"></i>
                            </a>
                        </div>
                        <input type="hidden" id="savegamedisplayname" value="<%= gameDisplayName %>" />
                        <input type="hidden" id="savegamename" value="<%= gameName %>" />
                        <input type="hidden" id="savegameid" value="<%= gameId %>" />
                        <input type="hidden" id="savegamenamespell" value="<%= gamenamespell %>" />
                    </div>
					<div class="row text-center">
                    <input id="btnPre" type="button" name="buttonPre" value=" 上一步 " onclick="back()" class="btn btn-primary">
					</div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <script type="text/javascript">
        function SelectItem(id) {
            var selgamename = document.getElementById('savegamename').value; //游戏简称
            var selgamedisplayname = document.getElementById('savegamedisplayname').value; //游戏名称
            var selgameid = document.getElementById('savegameid').value;     //游戏ID
            var selgamenamespell = document.getElementById('savegamenamespell').value;     //游戏全拼
            var selplatform = id;     //平台

            var urlParam = "?gameid=" + selgameid + "&gamename=" + selgamename + "&gamedisplayname=" + selgamedisplayname + "&gamenamespell=" + selgamenamespell;

            urlParam = urlParam + "&platform=" + selplatform;
            window.location.href = "./SelectGameVersionList.aspx" + urlParam;
        }

        function back() {
            var selgamename = document.getElementById('savegamename').value; //游戏简称
            var selgamedisplayname = document.getElementById('savegamedisplayname').value; //游戏名称
            var selgameid = document.getElementById('savegameid').value;     //游戏ID
            var selgamenamespell = document.getElementById('savegamenamespell').value;     //游戏全拼
            var urlParam = "?gameid=" + selgameid + "&gamename=" + selgamename + "&gamedisplayname=" + selgamedisplayname + "&gamenamespell=" + selgamenamespell;
            window.location.href = "./SelectGame.aspx" + urlParam;
        }
    </script>
</asp:Content>
