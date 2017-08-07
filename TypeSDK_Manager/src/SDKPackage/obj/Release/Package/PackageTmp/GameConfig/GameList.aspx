<%@ Page Title="接入游戏管理" Language="C#" MasterPageFile="~/Admin.Master" AutoEventWireup="true" CodeBehind="GameList.aspx.cs" Inherits="SDKPackage.GameConfig.GameList" %>

<asp:Content ID="Content1" ContentPlaceHolderID="MainContent" runat="server">
    <script src="/vendors/jquery.form/jquery.form.js"></script>
    <script type="text/javascript">
        $(function () {
            $('#MainContent_GameIocnFileUpload').change(function () {
                var fileName = $("#MainContent_GameIocnFileUpload").val();
                //判断上传文件的后缀名  
                var strExtension = fileName.substr(fileName.lastIndexOf('.') + 1);
                if (strExtension != 'jpg' && strExtension != 'gif'
                    && strExtension != 'png' && strExtension != 'bmp') {
                    alert("请选择图片文件");
                    return;
                }
                ajaxFileUpload();
            });
        })
        function ajaxFileUpload() {
            $("#ctl01").ajaxSubmit({
                url: '/Upload_Img.ashx?imgurl=game',
                beforeSubmit: function () {
                    $("#gameicon").attr("src", "/img/scz.gif");
                },
                success: function (json) {
                    json = eval("(" + json + ")");
                    if (json.success == "error") {
                        $("#gameicon").attr("src", "/img/upimg.jpg");
                        alert(json.msg);
                    }
                    else if (json.success == "success") {
                        $("#gameicon").attr("src", json.imgsrc);
                        $("#MainContent_hfGameIcon").val(json.imgsrc);
                    }
                }
            });
            return false;
        }
    </script>
    <script type="text/javascript">
        $(function () {
            $("#addGame").click(function () {
                $("#gametitle").text("添加游戏");
                $("#MainContent_hfSubmitType").val("add");
                whatShow();
                $("#MainContent_txtGameDisplayName option:first").prop("selected", 'selected');
                $("#MainContent_ddlAndroidVersionList option:first").prop("selected", 'selected');
                $("#MainContent_ddlIOSVersionList option:first").prop("selected", 'selected');
                $("#MainContent_txtGameDisplayName").val("");
                $("#MainContent_txtGameName").val("");
                $("#MainContent_txtGameNameSpell").val("");
                $("#MainContent_txtUnityVer").val("");
                $("#MainContent_txtProductName").val("");
                $("#gameicon").attr("src", "/img/upimg.jpg");
                $("#MainContent_hfGameIcon").val("");
            });
            $("#btnPlatform").click(function () {
                $("#gametitle").text("编辑渠道");
                $("#MainContent_hfSubmitType").val("platform");
                whatShow();
            });
        })
        function GetEditInfo(obj, operate) {
            $("#gametitle").text("修改游戏");
            $("#MainContent_hfSubmitType").val("edit");
            whatShow();
            $("#MainContent_hfgameID").val($(obj).parent("td").parent("tr").find("td").eq(7).text());
            var gamedisplayname = $(obj).parent("td").parent("tr").find("td").eq(0).text();
            var gamename = $(obj).parent("td").parent("tr").find("td").eq(1).text();
            var gameandroidkey = $(obj).parent("td").parent("tr").find("td").eq(2).text();
            var gameandroidversion = $(obj).parent("td").parent("tr").find("td").eq(5).text();
            var gameiosversion = $(obj).parent("td").parent("tr").find("td").eq(6).text();
            var gameicon = $(obj).parent("td").parent("tr").find("td").eq(8).text();
            var gameNameSpell = $(obj).parent("td").parent("tr").find("td").eq(9).text();
            var gameUnityVer = $(obj).parent("td").parent("tr").find("td").eq(10).text();
            var gameProductName = $(obj).parent("td").parent("tr").find("td").eq(11).text();
            var gameIsEncryption = $(obj).parent("td").parent("tr").find("td").eq(12).text();

            var sdkgameid = $(obj).parent("td").parent("tr").find("td").eq(13).text();
            var sdkgamekey = $(obj).parent("td").parent("tr").find("td").eq(14).text();

            $("#MainContent_txtGameDisplayName").val(gamedisplayname);
            $("#MainContent_txtGameName").val(gamename);
            $("#MainContent_ddlAndroidVersionList").val(gameandroidversion)
            $("#MainContent_ddlIOSVersionList").val(gameiosversion);
            $("#MainContent_ddlAndroidKeyList").val(gameandroidkey);
            $("#MainContent_txtGameNameSpell").val(gameNameSpell);
            $("#MainContent_txtUnityVer").val(gameUnityVer);
            $("#MainContent_txtProductName").val(gameProductName);
            $("#MainContent_txtSDKGameID").val(sdkgameid);
            $("#MainContent_txtSDKGameKey").val(sdkgamekey);
            if (gameIsEncryption == "1") {
                document.getElementById("MainContent_CheckBoxIsEncryption").checked = true;
            }
            else {
                document.getElementById("MainContent_CheckBoxIsEncryption").checked = false;
            }
            if (gameicon != "" && gameicon != undefined) {
                $("#gameicon").attr("src", gameicon);
                $("#MainContent_hfGameIcon").val(gameicon);
                $("#MainContent_hfIsGameIcon").val(gameicon);
            }
        }

        function whatShow() {
            var commandName = $("#MainContent_hfSubmitType").val();
            if (commandName == "info") {
                $("#divdivbasic").show();
                $("#divPlatform").show();
                $("#modalfooter1").hide();
                $("#modalfooter2").hide();
            }
            else if (commandName == "platform") {
                $("#divdivbasic").hide();
                $("#divPlatform").show();
                $("#modalfooter1").hide();
                $("#modalfooter2").show();
            }
            else {
                $("#divdivbasic").show();
                $("#divPlatform").hide();
                $("#modalfooter1").show();
                $("#modalfooter2").hide();
            }
        }
    </script>
    <!--添加游戏-->
    <div class="modal fade" id="divAddGame" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                        ×
                    </button>
                    <h3 class="modal-title" id="gametitle"></h3>
                </div>
                <div id="divbasic">
                    <div class="row" style="margin: 20px 0;">
                        <div class="col-md-6">
                            <label class="control-label col-md-4" style="text-align: right;">游戏名</label>
                            <div class="col-md-8">
                                <asp:TextBox ID="txtGameDisplayName" runat="server" placeholder="请输入游戏名称" CssClass="form-control"></asp:TextBox>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <label class="control-label col-md-4" style="text-align: right;">游戏简称</label>
                            <div class="col-md-8">
                                <asp:TextBox ID="txtGameName" runat="server" placeholder="请输入游戏简称" CssClass="form-control"></asp:TextBox>
                            </div>
                        </div>
                    </div>
                    <div class="row" style="margin: 20px 0;">
                        <div class="col-md-6">
                            <label class="control-label col-md-4" style="text-align: right;">游戏ID（SDK通信用）</label>
                            <div class="col-md-8">
                                <asp:TextBox ID="txtSDKGameID" runat="server" placeholder="请输入游戏ID" CssClass="form-control"></asp:TextBox>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <label class="control-label col-md-4" style="text-align: right;">游戏KEY（SDK通信用）</label>
                            <div class="col-md-8">
                                <asp:TextBox ID="txtSDKGameKey" runat="server" placeholder="请输入游戏KEY" CssClass="form-control"></asp:TextBox>
                            </div>
                        </div>
                    </div>
                    <div class="row" style="margin: 20px 0;">
                        <div class="col-md-6">
                            <label class="control-label col-md-4" style="text-align: right;">游戏全拼</label>
                            <div class="col-md-8">
                                <asp:TextBox ID="txtGameNameSpell" runat="server" placeholder="请输入游戏全拼" CssClass="form-control"></asp:TextBox>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <label class="control-label col-md-4" style="text-align: right;">Unity3D版本(IOS)</label>
                            <div class="col-md-8">
                                <asp:TextBox ID="txtUnityVer" runat="server" placeholder="请输入Unity3D软件版本" CssClass="form-control"></asp:TextBox>
                            </div>
                        </div>
                    </div>
                    <div class="row" style="margin-bottom: 18px;">
                        <div class="col-md-6">
                            <label class="control-label col-md-4" style="text-align: right;">SDK 版本 （Android）</label>
                            <div class="col-md-8">
                                <asp:DropDownList ID="ddlAndroidVersionList" runat="server" CssClass="form-control" Style="width: 150px; margin-right: 20px;">
                                </asp:DropDownList>
                            </div>

                        </div>
                        <div class="col-md-6">
                            <label class="control-label col-md-4" style="text-align: right;">SDK 版本 （IOS）</label>
                            <div class="col-md-8">
                                <asp:DropDownList ID="ddlIOSVersionList" runat="server" CssClass="form-control" Style="width: 150px; margin-right: 20px;">
                                </asp:DropDownList>
                            </div>
                        </div>
                    </div>
                    <div class="row" style="margin-bottom: 20px;">
                        <div class="col-md-6">
                            <label class="control-label col-md-4" style="text-align: right;">默认签名（Android）</label>
                            <div class="col-md-8">
                                <asp:DropDownList ID="ddlAndroidKeyList" runat="server" CssClass="form-control" Style="width: 150px; margin-right: 20px;">
                                </asp:DropDownList>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <label class="control-label col-md-4" style="text-align: right;">游戏图标(512px)</label>
                            <div class="col-md-8" style="position: relative;">
                                <div style="position: relative; width: 80px; height: 80px; overflow: hidden;">
                                    <img src="../img/upimg.jpg" style="width: 80px; height: 80px;" id="gameicon" />
                                    <asp:FileUpload ID="GameIocnFileUpload" runat="server" CssClass="form-control" Style="cursor: pointer; padding: 0; height: 80px; width: 80px; filter: alpha(opacity=0); -moz-opacity: 0; -khtml-opacity: 0; opacity: 0; position: absolute; top: 0;" />
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="row" style="margin: 20px 0;">
                        <div class="col-md-6">
                            <label class="control-label col-md-4" style="text-align: right;">产品名称 （IOS）</label>
                            <div class="col-md-8">
                                <asp:TextBox ID="txtProductName" runat="server" placeholder="请输入ProductName" CssClass="form-control"></asp:TextBox>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <label class="control-label col-md-4" style="text-align: right;">Unity加密</label>
                            <div class="col-md-8">
                                <asp:CheckBox ID="CheckBoxIsEncryption" runat="server" />
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer" id="modalfooter1">
                        <button type="button" class="btn btn-default" data-dismiss="modal">
                            关闭
                        </button>
                        <asp:Button ID="ButtonAddGame" runat="server" class="btn btn-primary" Text="保存" OnClick="ButtonAddGame_Click" />
                    </div>
                </div>
                <div id="divPlatform">
                    <div class="row" style="margin-bottom: 20px;">
                        <div class="col-md-2">
                            <label class="control-label col-md-12" style="text-align: right;">Android 渠道</label>

                        </div>
                        <div class="col-md-8">
                            <div><a class="btn btn-info " data-toggle="modal" data-target="#divplatform">设定 </a></div>
                            <div></div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-2">
                            <label class="control-label col-md-12" style="text-align: right;">IOS 渠道</label>

                        </div>
                        <div class="col-md-8">
                            <div><a class="btn btn-info ">设定 </a></div>
                            <div></div>
                        </div>
                    </div>
                    <div class="modal-footer" id="modalfooter2">
                        <button type="button" class="btn btn-default" data-dismiss="modal">
                            关闭
                        </button>
                        <asp:Button ID="ButtonAddPlatform" runat="server" class="btn btn-primary" Text="保存" />
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!--添加游戏end-->
    <!--渠道-->
    <div class="modal fade" id="divplatform" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                        ×
                    </button>
                    <h2 class="modal-title" id="platform">选择渠道</h2>
                </div>
                <div>
                    渠道列表
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        关闭
                    </button>
                    <asp:Button ID="Button2" runat="server" class="btn btn-primary" Text="保存" />
                </div>
            </div>
        </div>
    </div>
    <!--渠道end-->
    <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
            <div class="x_panel">
                <div class="x_title">
                    <h2>接入游戏管理</h2>
                    <ul class="nav navbar-right panel_toolbox">
                      <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
                      </li>
                    </ul>
                <div class="clearfix"></div>
                </div>
                <div class="x_content">

                    <div>
                        <a id="addGame" class="btn btn-primary" data-toggle="modal" href="#divAddGame">新增游戏</a>
                        <asp:Label ID="MessageLabel" runat="server" Text="" Style="color: #f00;"></asp:Label>
                        
                    </div>
                    <asp:ListView ID="ListView1" runat="server" OnItemCommand="ListView1_ItemCommand">
                        <EmptyDataTemplate>
                            <table>
                                <tr>
                                    <td>没有任何游戏。</td>
                                </tr>
                            </table>
                        </EmptyDataTemplate>
                        <LayoutTemplate>
                            <table id="itemGamesContainer" class="table table-striped jambo_table">
                                <thead>
                                    <tr>
                                        <th>游戏名</th>
                                        <th>游戏简称</th>
                                        <th>Android SDK 版本</th>
                                        <th>IOS SDK 版本</th>
                                        <th style="display: none;"></th>
                                        <th style="display: none;"></th>
                                        <th style="display: none;"></th>
                                        <th style="display: none;"></th>
                                        <th style="display: none;"></th>
                                        <th>SDKGameID</th>
                                        <th>SDKGameKey</th>
                                        <th>操作</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr id="itemPlaceholder" runat="server">
                                    </tr>
                                </tbody>
                            </table>
                        </LayoutTemplate>
                        <ItemTemplate>
                            <tr>
                                <td><%#Eval("GameDisplayName") %></td>
                                <td><%#Eval("GameName") %></td>
                                <td style="display: none"><%#Eval("AndroidKeyID") %></td>
                                <td><%#Eval("androidversion") %></td>
                                <td><%#Eval("iosversion") %></td>
                                <td style="display: none"><%#Eval("AndroidVersionID") %></td>
                                <td style="display: none"><%#Eval("IOSVersionID") %></td>
                                <td style="display: none"><%#Eval("GameID") %></td>
                                <td style="display: none"><%#Eval("GameIcon") %></td>
                                <td style="display: none"><%#Eval("GameNameSpell") %></td>
                                <td style="display: none"><%#Eval("UnityVer") %></td>
                                <td style="display: none"><%#Eval("ProductName") %></td>
                                <td style="display: none"><%#Eval("IsEncryption") %></td>
                                <td><%#Eval("SDKGameID") %></td>
                                <td><%#Eval("SDKGameKey") %></td>
                                <td>
                                    <button id="btnEdit" type="button" data-toggle="modal" class="btn btn-primary btn-sm fa" data-target="#divAddGame" onclick='GetEditInfo(this,"1")'><i class='fa fa-edit'></i> 编辑 </button>

                                    <asp:Button ID="btnDelete" runat="server" class="fa fa-trash" Text="&#xf014; 删除" CommandName="del" CommandArgument='<%#Eval("GameID") + "_" + Eval("GameName") + "_" + Eval("GameNameSpell") %>' CssClass="btn btn-danger btn-sm fa" OnClientClick="return confirm('确定要删除数据吗？')" />

                                    <a href='AddGamePlatfrom.aspx?gameid=<%#Eval("GameID") %>&androidversionid=<%#Eval("AndroidVersionID") %>&iosversionid=<%#Eval("IOSVersionID") %>&gamedisplayname=<%#Eval("GameDisplayName") %>' class="btn btn-info btn-sm fa"><i class='fa fa-check-square'></i> 接入渠道</a>
                                </td>
                            </tr>
                        </ItemTemplate>
                    </asp:ListView>
                </div>
            </div>
        </div>
    </div>


    <input id="hfSubmitType" type="hidden" runat="server" />
    <asp:HiddenField ID="hfgameID" runat="server" />
    <asp:HiddenField ID="hfGameIcon" runat="server" />
    <asp:HiddenField ID="hfIsGameIcon" runat="server" />
</asp:Content>
