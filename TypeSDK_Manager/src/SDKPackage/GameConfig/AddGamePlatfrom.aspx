<%@ Page Title="游戏渠道列表" Language="C#" MasterPageFile="~/Admin.Master" AutoEventWireup="true" CodeBehind="AddGamePlatfrom.aspx.cs" Inherits="SDKPackage.GameConfig.AddGamePlatfrom" %>

<asp:Content ID="Content1" ContentPlaceHolderID="MainContent" runat="server">
    <script type="text/javascript">
        $(document).ready(function () {
            //android 渠道列表展开
            var androidHiddenlist = $("#ListViewAndroid_itemGamesContainer input[name='hfPlatformID']");
            $("#addAndroidPlatform").click(function () {
                var platformIdList = "";
                for (var i = 0; i < androidHiddenlist.length; i++) {
                    platformIdList = platformIdList + androidHiddenlist[i].value + ",";
                }
                platformIdList = platformIdList.substring(0, platformIdList.length - 1);
                console.log(platformIdList);
                if (platformIdList.length > 0)
                    checkPlatform(platformIdList);
                else {
                    $("#ListViewAndroidPlatformList_itemGamesContainer input[name='hfPlatformID']").each(function () {
                        $(this).attr("checked", false);
                    });
                }
            });

            var iosHiddenlist = $("#ListViewIOS_itemGamesContainer input[name='hfPlatformID']");
            $("#addIOSPlatform").click(function () {
                var platformIdList = "";
                for (var i = 0; i < iosHiddenlist.length; i++) {
                    platformIdList = platformIdList + iosHiddenlist[i].value + ",";
                }
                platformIdList = platformIdList.substring(0, platformIdList.length - 1);
                if (platformIdList.length > 0)
                    checkPlatformIOS(platformIdList);
                else {
                    $("#ListViewIOSPlatformList_itemGamesContainer input[name='hfPlatformID']").each(function () {
                        $(this).attr("checked", false);
                    });
                }
            });

            $("#MainContent_ButtonAddAndroidPlatform").click(function () {
                var checkboxList = $("#ListViewAndroidPlatformList_itemGamesContainer input[type='checkbox']:checked");
                var strhidden = "";
                var strsign = "";
                var strVersion = "";
                for (var i = 0; i < checkboxList.length; i++) {
                    if (checkboxList[i].checked) {
                        strhidden = strhidden + checkboxList[i].value + ",";
                        strsign = strsign + $(checkboxList[i]).parent("div").parent("td").parent("tr").children("td").eq(3).children("select").val() + ",";
                        if ($(checkboxList[i]).parent("div").parent("td").parent("tr").children("td").eq(1).children("select").val() == null || $(checkboxList[i]).parent("div").parent("td").parent("tr").children("td").eq(1).children("select").val() == undefined) {
                            alert("未配置渠道版本");
                            return false;
                        }
                        strVersion = strVersion + $(checkboxList[i]).parent("div").parent("td").parent("tr").children("td").eq(1).children("select").val() + ",";
                    }
                }
                if (strhidden.length > 0) {
                    strhidden = strhidden.substring(0, strhidden.length - 1);
                    strsign = strsign.substring(0, strsign.length - 1);
                    strVersion = strVersion.substring(0, strVersion.length - 1);
                }
                $("#MainContent_hfAndroidPlatformList").val(strhidden);
                $("#MainContent_hfAndroidSignList").val(strsign);
                $("#MainContent_hfAndroidVersionList").val(strVersion);
            });


            $("#MainContent_ButtonAddIOSPlatform").click(function () {
                var checkboxList = $("#ListViewIOSPlatformList_itemGamesContainer input[type='checkbox']:checked");
                var strhidden = "";
                var strVersion = "";
                for (var i = 0; i < checkboxList.length; i++) {
                    if (checkboxList[i].checked) {
                        strhidden = strhidden + checkboxList[i].value + ",";
                        strVersion = strVersion + $(checkboxList[i]).parent("div").parent("td").parent("tr").children("td").eq(1).children("select").val() + ",";
                    }
                }
                if (strhidden.length > 0){
                    strhidden = strhidden.substring(0, strhidden.length - 1);
                    strVersion = strVersion.substring(0, strVersion.length - 1);
                }
                $("#MainContent_hfIOSPlatformList").val(strhidden);
                $("#MainContent_hfIOSVersionList").val(strVersion);
            });
        })

        function checkPlatform(platformlist) {
                    var arrList = platformlist.split(',');
                    var checkboxList = $("#ListViewAndroidPlatformList_itemGamesContainer input[name='hfPlatformID']");
        //            var pvidlist = $("#ListViewAndroid_itemGamesContainer input[name='hfpvid']");
                    var pfidlist = $("#ListViewAndroid_itemGamesContainer input[name='hfPlatformID']");
                    var signlist = $("#ListViewAndroid_itemGamesContainer input[name='hfKeyID']");
                    var tempName;
                    for (var j = 0; j < checkboxList.length; j++) {
                        for (var i = 0; i < arrList.length; i++) {
                            if (arrList[i] == checkboxList[j].value) {
                                $(checkboxList[j]).parent("div").addClass("checked");
                                $(checkboxList[j]).attr("checked", true);

                                for (var k = 0; k < pfidlist.length; k++) {
                                    if (pfidlist[k].value == checkboxList[j].value) {
                                        $(checkboxList[j]).closest("tr").find("td:nth-child(2) > select").val($(pfidlist[k]).closest("tr").find("td:nth-child(3) > input").val());//version list 选中
                                        $(checkboxList[j]).closest("tr").find("td:nth-child(4) > select").val($(pfidlist[k]).closest("tr").find("td:nth-child(4) > input").val());//sign list 选中
                                    }
                                }
                                break;
                            }
                            $(checkboxList[j]).next("input:checkbox").attr("checked", false);
                        }
                    }
                }

        function checkPlatformIOS(platformlist) {
            var arrList = platformlist.split(',');
            var checkboxList = $("#ListViewIOSPlatformList_itemGamesContainer input[name='hfPlatformID']");
            var pfidlist = $("#ListViewIOS_itemGamesContainer input[name='hfPlatformID']");
            for (var j = 0; j < checkboxList.length; j++) {
                for (var i = 0; i < arrList.length; i++) {
                    if (arrList[i] == checkboxList[j].value) {
                        //alert(arrList[i]);
                        $(checkboxList[j]).parent("div").addClass("checked");
                        $(checkboxList[j]).attr("checked", true);

                        for (var k = 0; k < pfidlist.length; k++) {
                            if (pfidlist[k].value == checkboxList[j].value) {
                                $(checkboxList[j]).closest("tr").find("td:nth-child(2) > select").val($(pfidlist[k]).closest("tr").find("td:nth-child(3) > input").val());//version list 选中
                            }
                        }

                        break;
                    }
                    $(checkboxList[j]).next("input:checkbox").attr("checked", false);
                }
            }
        }
    </script>

    <!-- Datatables -->
    <link href="/vendors/datatables.net-bs/css/dataTables.bootstrap.min.css" rel="stylesheet">
    <link href="/vendors/datatables.net-buttons-bs/css/buttons.bootstrap.min.css" rel="stylesheet">
    <link href="/vendors/datatables.net-fixedheader-bs/css/fixedHeader.bootstrap.min.css" rel="stylesheet">
    <link href="/vendors/datatables.net-responsive-bs/css/responsive.bootstrap.min.css" rel="stylesheet">
    <link href="/vendors/datatables.net-scroller-bs/css/scroller.bootstrap.min.css" rel="stylesheet">
    <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
            <div class="nav navbar-right">
                        <a class="btn btn-primary " data-toggle="modal" data-target="#divGameSDKRedisList" id="syncRedis"><i class="fa fa-check-square-o"></i> 同步SDK服务器</a>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
            <div class="x_panel">
                <div class="x_title">
                    <h2><%= gamedisplayname %> Android渠道设定</h2>
                    <ul class="nav navbar-right panel_toolbox">
                      <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
                      </li>
                    </ul>
                <div class="clearfix"></div>
                </div>
                <div class="x_content">
                    <div>
                        <a class="btn btn-primary " data-toggle="modal" data-target="#divandroidplatform" id="addAndroidPlatform"><i class="fa fa-check-square-o"></i> 选择游戏接入渠道</a>
                    
                        <asp:Label ID="lblSyncRedis" runat="server" Text=""></asp:Label>
                    </div>
                    <asp:ListView ID="ListViewAndroid" runat="server" DataSourceID="SqlDataSourceAndroid">
                        <LayoutTemplate>
                            <table id="ListViewAndroid_itemGamesContainer" class="table table-striped jambo_table">
                                <thead>
                                    <tr>
                                        <th>渠道名</th>
                                        <th>渠道简称</th>
                                        <th>渠道 SDK 版本</th>
                                        <th>选择签名</th>
                                        <th>渠道参数</th>
                                        <th>渠道图标</th>
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
                                <td><%#Eval("PlatformDisplayName") %>
                                    <input type="hidden" value='<%#Eval("platformid") %>' name="hfPlatformID" />
                                </td>
                                <td><%#Eval("PlatformName") %></td>
                                <td><%#Eval("Version") %><input type="hidden" value='<%#Eval("pvid") %>' name="hfpvid" /></td>
                                <td><%#Eval("KeyName") %><input type="hidden" value='<%#Eval("SignatureKeyID") %>' name="hfKeyID" /></td>
                                <td><a class="btn btn-primary btn-sm fa" href='GamePlatformConfig.aspx?gameid=<%#Eval("GameID") %>&platformid=<%#Eval("platformid") %>&SignatureKeyID=<%#Eval("SignatureKeyID") %>&systemid=1&gamedisplayname=<%#Eval("GameDisplayName") %>&gamename=<%#Eval("GameName") %>&platformname=<%#Eval("PlatformName") %>&androidversionid=<%=androidversionid %>&iosversionid=<%=iosversionid %>&pluginid=<%#Eval("pluginid") %>'><i class='fa fa-edit'></i> 参数配置</a></td>
                                <td><a class="btn btn-primary btn-sm fa" href='GamePlatformIcon.aspx?gameid=<%#Eval("GameID") %>&platformid=<%#Eval("platformid") %>&SignatureKeyID=<%#Eval("SignatureKeyID") %>&systemid=1&gamedisplayname=<%#Eval("GameDisplayName") %>&gamename=<%#Eval("GameName") %>&platformname=<%#Eval("PlatformName") %>&androidversionid=<%=androidversionid %>&iosversionid=<%=iosversionid %>&pluginid=<%#Eval("pluginid") %>'><i class='fa fa-edit'></i> 图标配置</a></td>
                            </tr>
                        </ItemTemplate>
                    </asp:ListView>
                </div>
            </div>
        </div>
        <div class="col-md-12 col-sm-12 col-xs-12">
            <div class="x_panel">
                <div class="x_title">
                    <h2><%= gamedisplayname %> IOS渠道设定</h2>
                    <ul class="nav navbar-right panel_toolbox">
                      <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
                      </li>
                    </ul>
                <div class="clearfix"></div>
                </div>
                <div class="x_content">
                    <div>
                        <a class="btn btn-primary " data-toggle="modal" data-target="#diviosplatform" id="addIOSPlatform"><i class="fa fa-check-square-o"></i> 选择接入渠道</a>
                    </div>
                    <asp:ListView ID="ListViewIOS" runat="server" DataSourceID="SqlDataSourceIOS">
                        <LayoutTemplate>
                            <table id="ListViewIOS_itemGamesContainer" class="table table-striped jambo_table">
                                <thead>
                                    <tr runat="server">
                                        <th>渠道名</th>
                                        <th>渠道简称</th>
                                        <th>渠道 SDK 版本</th>
                                        <th>渠道参数</th>
                                        <th>渠道图标</th>
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
                                <td><%#Eval("PlatformDisplayName") %>
                                    <input type="hidden" value='<%#Eval("platformid") %>' name="hfPlatformID" /></td>
                                <td><%#Eval("PlatformName") %></td>
                                <td><%#Eval("Version") %><input type="hidden" value='<%#Eval("pvid") %>' name="hfpvid" /></td>
                                <td><a class="btn btn-primary btn-sm fa" href='GamePlatformConfig.aspx?gameid=<%#Eval("GameID") %>&platformid=<%#Eval("platformid") %>&SignatureKeyID=<%#Eval("SignatureKeyID") %>&systemid=2&gamedisplayname=<%#Eval("GameDisplayName") %>&gamename=<%#Eval("GameName") %>&platformname=<%#Eval("PlatformName") %>&androidversionid=<%=androidversionid %>&iosversionid=<%=iosversionid %>&pluginid=0'><i class='fa fa-edit'></i> 参数配置</a></td>
                                <td><a class="btn btn-primary btn-sm fa" href='GamePlatformIcon.aspx?gameid=<%#Eval("GameID") %>&platformid=<%#Eval("platformid") %>&SignatureKeyID=<%#Eval("SignatureKeyID") %>&systemid=2&gamedisplayname=<%#Eval("GameDisplayName") %>&gamename=<%#Eval("GameName") %>&platformname=<%#Eval("PlatformName") %>&androidversionid=<%=androidversionid %>&iosversionid=<%=iosversionid %>&pluginid=0'><i class='fa fa-edit'></i> 图标配置</a></td>
                            </tr>
                        </ItemTemplate>
                    </asp:ListView>
                </div>
            </div>
            <div class="text-center">
                <a class="btn btn-default" href="GameList.aspx"><i class="fa fa-reply"></i> 返回</a>
            </div>
        </div>
    </div>
    <asp:SqlDataSource ID="SqlDataSourceAndroid" runat="server" ConnectionString="<%$ ConnectionStrings:SdkPackageConnString %>" SelectCommand="sdk_getGamePlatformList_Android" SelectCommandType="StoredProcedure">
        <SelectParameters>
            <asp:QueryStringParameter QueryStringField="gameid" Name="GameID" Type="Int32" />
            <asp:Parameter Name="SystemID" Type="Int32" DefaultValue="1" />
        </SelectParameters>
    </asp:SqlDataSource>
    <asp:SqlDataSource ID="SqlDataSourceIOS" runat="server" ConnectionString="<%$ ConnectionStrings:SdkPackageConnString %>" SelectCommand="sdk_getGamePlatformList_IOS" SelectCommandType="StoredProcedure">
        <SelectParameters>
            <asp:QueryStringParameter QueryStringField="gameid" Name="GameID" Type="Int32" />
            <asp:Parameter Name="SystemID" Type="Int32" DefaultValue="2" />
        </SelectParameters>
    </asp:SqlDataSource>
    <!--android渠道列表-->
    <div class="modal fade" id="divandroidplatform" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                        ×
                    </button>
                    <h2 class="modal-title">Android 渠道设定</h2>
                </div>
                <!--内容-->
                <div class="modal-body">
                    <asp:ListView ID="ListViewAndroidPlatformList" runat="server" DataSourceID="SqlDataSourceAndroidPlatformList">
                        <LayoutTemplate>
                            <table id="ListViewAndroidPlatformList_itemGamesContainer" class="table table-striped jambo_table bulk_action">
                                <thead>
                                    <tr class="headings" style="left: 30px; position: relative;">
                                        <th runat="server">渠道名</th>
                                        <th runat="server">渠道 SDK 版本</th>
                                        <th runat="server">支持最高版本</th>
                                        <th runat="server">Android签名选择</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr id="itemPlaceholder" runat="server">
                                    </tr>
                                </tbody>
                            </table>
                        </LayoutTemplate>
                        <ItemTemplate>
                            <tr style="left: 5px; position: relative;">
                                <td>
                                    <input type="checkbox" class="flat" value='<%#Eval("dpfid") %>' name="hfPlatformID" />
                                    <%#Eval("PlatformDisplayName") %>
                                </td>
                                <td>
                                    <asp:DropDownList ID="DropDownList1" runat="server" DataSource='<%#GetAndroidPlatformVersion(Eval("dpfid").ToString()) %>' DataTextField="Version" DataValueField="id">
                                    </asp:DropDownList>

                                </td>
                                <td><%#Eval("SdkVersion") %></td>
                                <td>
                                    <asp:DropDownList ID="ddlSignatureKey" runat="server" DataSourceID="SqlDataSourceSignatureKey" DataTextField="KeyName" DataValueField="ID" name="SignatureKey">
                                    </asp:DropDownList></td>
                            </tr>
                        </ItemTemplate>
                    </asp:ListView>
                </div>
                <!--内容-->
                <div class="modal-footer" id="modalfooter">
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        关闭
                    </button>
                    <asp:Button ID="ButtonAddAndroidPlatform" runat="server" class="btn btn-primary" Text="保存" OnClick="ButtonAddAndroidPlatform_Click" />
                </div>
            </div>
        </div>
    </div>
    <!--android渠道列表end-->

    <!--iOS渠道列表-->
    <div class="modal fade" id="diviosplatform" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                        ×
                    </button>
                    <h2 class="modal-title">iOS 渠道设定</h2>
                </div>
                <!--内容-->
                <div class="modal-body">
                    <asp:ListView ID="ListViewIOSPlatformList" runat="server" DataSourceID="SqlDataSourceIOSPlatformList">
                        <LayoutTemplate>
                            <table id="ListViewIOSPlatformList_itemGamesContainer" class="table table-striped jambo_table">
                                <thead>
                                    <tr style="left: 30px; position: relative;">
                                        <th runat="server">渠道名</th>
                                        <th runat="server">渠道 SDK 版本</th>
                                        <th runat="server">支持最高版本</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr id="itemPlaceholder" runat="server">
                                    </tr>
                                </tbody>
                            </table>
                        </LayoutTemplate>
                        <ItemTemplate>
                            <tr style="left: 5px; position: relative;">
                                <td>
                                    <input type="checkbox" class="flat" value='<%#Eval("dpfid") %>' name="hfPlatformID" />
                                    <%#Eval("PlatformDisplayName") %>
                                </td>
                                <td>
                                    <asp:DropDownList ID="DropDownList1" runat="server" DataSource='<%#GetIosPlatformVersion(Eval("dpfid").ToString()) %>' DataTextField="Version" DataValueField="id">
                                    </asp:DropDownList>

                                </td>
                                <td><%#Eval("SdkVersion") %></td>
                        </ItemTemplate>
                    </asp:ListView>
                </div>
                <!--内容-->
                <div class="modal-footer" id="modalfooterios">
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        关闭
                    </button>
                    <asp:Button ID="ButtonAddIOSPlatform" runat="server" class="btn btn-primary" Text="保存" OnClick="ButtonAddIOSPlatform_Click" />
                </div>
            </div>
        </div>
    </div>
    <!--iOS渠道列表end-->

    <!--SDK Redis服务器列表-->
    <div class="modal fade" id="divGameSDKRedisList" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                        ×
                    </button>
                    <h2 class="modal-title">同步服务器配置</h2>
                </div>
                <!--内容-->
                <div class="modal-body">
                    <table id="ListViewAndroidPlatformList_itemGamesContainer" class="table table-striped jambo_table">
                        <thead>
                            <tr style="left: 30px; position: relative;">
                                <th runat="server">SDK HOST</th>
                                <th runat="server">PORT</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr style="left: 5px; position: relative;">
                                <td>
                                    <asp:TextBox ID="txtSDKHost" style="width:100px;" runat="server" Text="localhost"/>
                                </td>
                                <td>
                                    <asp:TextBox ID="txtSDKPort" style="width:100px;" runat="server"  Text="40000"/>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <!--内容-->
                <div class="modal-footer" id="modalfooterSDKRedisList">
                    
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        关闭
                    </button>
                    <asp:Button ID="ButtonSyncRedis" runat="server" class="btn btn-primary" Text="同步" OnClick="ButtonSyncRedis_Click" />
                </div>
            </div>
        </div>
    </div>
    <!--SDK Redis服务器列表end-->

    

    <asp:SqlDataSource ID="SqlDataSourceSignatureKey" runat="server" ConnectionString="<%$ ConnectionStrings:SdkPackageConnString %>" SelectCommand="select [Id],[KeyName] from [sdk_SignatureKey]" SelectCommandType="Text"></asp:SqlDataSource>
    <asp:SqlDataSource ID="SqlDataSourceAndroidPlatformList" runat="server" ConnectionString="<%$ ConnectionStrings:SdkPackageConnString %>"
        SelectCommand="sdk_getPlatformListInfo"
        SelectCommandType="StoredProcedure">
        <SelectParameters>
            <asp:QueryStringParameter QueryStringField="androidversionid" Name="SdkVersion" Type="String" />
            <asp:Parameter Name="SystemID" DefaultValue="1" />
        </SelectParameters>
    </asp:SqlDataSource>
    <asp:SqlDataSource ID="SqlDataSourceIOSPlatformList" runat="server" ConnectionString="<%$ ConnectionStrings:SdkPackageConnString %>"
        SelectCommand="sdk_getPlatformListInfo"
        SelectCommandType="StoredProcedure">
        <SelectParameters>
            <asp:QueryStringParameter QueryStringField="iosversionid" Name="SdkVersion" Type="String" />
            <asp:Parameter Name="SystemID" DefaultValue="2" />
        </SelectParameters>
    </asp:SqlDataSource>
    <asp:HiddenField ID="hfAndroidPlatformList" runat="server" />
    <asp:HiddenField ID="hfAndroidSignList" runat="server" />
    <asp:HiddenField ID="hfAndroidVersionList" runat="server" />
    <asp:HiddenField ID="hfLeBianAndroidPlatformList" runat="server" />
    <asp:HiddenField ID="hfLeBianAndroidSignList" runat="server" />
    <asp:HiddenField ID="hfLeBianAndroidVersionList" runat="server" />
    <asp:HiddenField ID="hfInitSignList" runat="server" />
    <asp:HiddenField ID="hfIOSPlatformList" runat="server" />
    <asp:HiddenField ID="hfIOSVersionList" runat="server" />    <!-- Datatables -->
    <script src="/vendors/datatables.net/js/jquery.dataTables.min.js"></script>
    <script src="/vendors/datatables.net-bs/js/dataTables.bootstrap.min.js"></script>
    <script src="/vendors/datatables.net-buttons/js/dataTables.buttons.min.js"></script>
    <script src="/vendors/datatables.net-buttons-bs/js/buttons.bootstrap.min.js"></script>
    <script src="/vendors/datatables.net-buttons/js/buttons.flash.min.js"></script>
    <script src="/vendors/datatables.net-buttons/js/buttons.html5.min.js"></script>
    <script src="/vendors/datatables.net-buttons/js/buttons.print.min.js"></script>
    <script src="/vendors/datatables.net-fixedheader/js/dataTables.fixedHeader.min.js"></script>
    <script src="/vendors/datatables.net-keytable/js/dataTables.keyTable.min.js"></script>
    <script src="/vendors/datatables.net-responsive/js/dataTables.responsive.min.js"></script>
    <script src="/vendors/datatables.net-responsive-bs/js/responsive.bootstrap.js"></script>
    <script src="/vendors/datatables.net-scroller/js/datatables.scroller.min.js"></script>
    <script src="/vendors/jszip/dist/jszip.min.js"></script>
    <script src="/vendors/pdfmake/build/pdfmake.min.js"></script>
    <script src="/vendors/pdfmake/build/vfs_fonts.js"></script>
    
    <!-- Datatables -->
    <script>
        $(document).ready(function() {
        $('#ListViewAndroid_itemGamesContainer').dataTable({
            "paging": false,
            "order": [[ 1, "asc" ]],
            "sPaginationType" : "full_numbers",
            "aoColumnDefs" : [ { "bSortable": false, "aTargets": [0,2,3,4,5] } ],
            "oLanguage" : {
                "sLengthMenu": "每页显示 _MENU_ 条记录",
                "sZeroRecords": "抱歉， 没有找到",
                "sInfo": "从 _START_ 到 _END_ /共 _TOTAL_ 条数据",
                "sInfoEmpty": "没有数据",
                "sInfoFiltered": "(从 _MAX_ 条数据中检索)",
                "sZeroRecords": "没有检索到数据",
                "sSearch": "搜索:",
                "oPaginate": {
                "sFirst": "首页",
                "sPrevious": "前一页",
                "sNext": "后一页",
                "sLast": "尾页"
                }
            }
        });
        });
    </script>
</asp:Content>
