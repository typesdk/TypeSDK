<%@ Page Title="SDK打包管理系统" Language="C#" MasterPageFile="~/Admin.Master" AutoEventWireup="true" CodeBehind="SelectPlace.aspx.cs" Inherits="SDKPackage.PJPackage.SelectPlace" %>

<asp:Content ID="BodyContent" ContentPlaceHolderID="MainContent" runat="server">
    <!-- Datatables -->
    <link href="/vendors/datatables.net-bs/css/dataTables.bootstrap.min.css" rel="stylesheet">
    <link href="/vendors/datatables.net-buttons-bs/css/buttons.bootstrap.min.css" rel="stylesheet">
    <link href="/vendors/datatables.net-fixedheader-bs/css/fixedHeader.bootstrap.min.css" rel="stylesheet">
    <link href="/vendors/datatables.net-responsive-bs/css/responsive.bootstrap.min.css" rel="stylesheet">
    <link href="/vendors/datatables.net-scroller-bs/css/scroller.bootstrap.min.css" rel="stylesheet">

    <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
            <div class="x_panel">
                <div class="x_title">
                    <h2>选择渠道</h2>
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
                          <a href="#step-4" class="selected" isdone="0" rel="4">
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
                        <div class="form-group">
                            <span class="name">版本:</span>
                            <span class="value text-primary"><%= gameversion %></span>
                        </div>
                    </div>

                    <div>
                        <asp:ListView ID="GamePlaceList" runat="server" DataSource="<%# Verification()%>">

                            <EmptyDataTemplate>
                                <table>
                                    <tr>
                                        <td>没有可用渠道,请至游戏管理中选择并配置渠道。</td>
                                    </tr>
                                </table>
                            </EmptyDataTemplate>

                            <ItemTemplate>
                                <tr>
                                <td>
                                        <input type="checkbox" class="flat" value="<%#Eval("Id")+"_"+Eval("PlugInID") %>" <%#Eval("Nullity").ToString()=="1"?"disabled='true'":Eval("iconFlag").ToString()=="0"?"disabled='true'":Eval("error").ToString()!=""?"disabled='true'":"name='table_records'" %> /></td>
                                    <td>
                                        <%#Eval("PlugInID").ToString() == "1"?"乐变"+ Eval("PlatformDisplayName")+"渠道":Eval("PlatformDisplayName")%></td>

                                    <td><%#Eval("PlugInID").ToString() == "1"?Eval("PlatformName")+"_LeBian":Eval("PlatformName")%></td>

                                    <td><%#Eval("Version") %><input type="hidden" value='<%#Eval("PlugInID") %>' />
                                    </td>

                                    <td><%#Eval("Nullity").ToString()=="1"?"<span style='color:#f00;'>渠道维护中</span>":Eval("iconFlag").ToString()=="0"?"<span style='color:#f00;'>该渠道还没有配置图标组</span>":Eval("error").ToString()==""?"可以打包":"<span style='color:#f00;'>"+Eval("error").ToString()+"</span>"%></td>

                                </tr>
                            </ItemTemplate>

                            <LayoutTemplate>
                                <table id="itemPlaceholderContainer" class="table table-striped jambo_table bulk_action">
                                    <thead>
                                        <tr class="headings">
                            <th>
                              <input type="checkbox" id="check-all" class="flat">
                            </th>
                                            <th>渠道名称</th>
                                            <th>渠道编号</th>
                                            <th>渠道版本</th>
                                            <th>渠道状态</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr id="itemPlaceholder" runat="server">
                                        </tr>
                                    </tbody>
                                </table>
                            </LayoutTemplate>
                        </asp:ListView>
                        <asp:SqlDataSource ID="SqlDataSourceGamePlaceList" runat="server" ConnectionString="<%$ ConnectionStrings:SdkPackageConnString %>" SelectCommand="sdk_getGamePlatforms" SelectCommandType="StoredProcedure">
                            <SelectParameters>
                                <asp:QueryStringParameter Name="GameID" Type="Int32" QueryStringField="gameid" />
                                <asp:QueryStringParameter Name="SystemID" Type="String" QueryStringField="platform" />
                            </SelectParameters>
                        </asp:SqlDataSource>
                        <div class="text-center">
                            <input type="hidden" id="savegamedisplayname" value="<%= gameDisplayName %>" />
                            <input type="hidden" id="savegamename" value="<%= gameName %>" />
                            <input type="hidden" id="savegameid" value="<%= gameId %>" />
                            <input type="hidden" id="savegamenamespell" value="<%= gamenamespell %>" />
                            <input type="hidden" id="saveplatform" value="<%= platform %>" />
                            <input type="hidden" id="savetaskid" value="<%= taskid %>" />
                            <input type="hidden" id="savegameversion" value="<%= gameversion %>" />
                            <input type="hidden" id="savegamelable" value="<%= gamelable %>" />
                            <input id="btnPre" type="button" name="buttonPre" value=" 上一步 " onclick="back()" class="btn btn-primary">&nbsp&nbsp
        <input id="btnNext" type="button" name="buttonNext" value="  下一步  " onclick="NextStep()" class="btn btn-primary">
                            <input id="hfPlatformVersionList" type="hidden" />
                            <input id="hfPlugInIDList" type="hidden" />
                        </div>
                    </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script type="text/javascript">

        //页面显示时，默认未选择
        var obj = document.getElementsByName('options');
        for (i = 0; i < obj.length; i++) {
            obj[i].checked = false;
        }

       <% if (isBack)
          { %>
        var obj = document.getElementsByName('table_records');
        var placeidlist = '<%= placeidlist %>';
                            placeidlist = placeidlist.split(',');
                            //$.each(placeidlist, function (item) {
                            //    alert(item);
                            //})
                            for (i = 0; i < obj.length; i++) {
                                for (var j = 0; j < placeidlist.length; j++) {
                                    if (obj[i].value == placeidlist[j]) {
                                        obj[i].checked = true;
                                    }
                                }
                            }
        <% } %>

        function NextStep() {
            var stridlist = GetRadioCheckInfo();

            if (stridlist == "") {
                alert("抱歉，没有选择渠道！");
                return;
            }
            var selgameid = "";
            var selgamename = "";
            var selgamedisplayname = "";
            var selgamenamespel = "";
            var selplatform = "";
            var seltaskid = "";
            var selgameversion = "";
            var selgamelable = "";
            var selplatformversionlist = "";
            var selpluginidlist = "";

            selgamename = document.getElementById('savegamename').value; //游戏简称
            selgamedisplayname = document.getElementById('savegamedisplayname').value; //游戏名称
            selgameid = document.getElementById('savegameid').value;     //游戏ID
            selgamenamespell = document.getElementById('savegamenamespell').value;     //游戏全拼
            selplatform = document.getElementById('saveplatform').value; //平台
            seltaskid = document.getElementById('savetaskid').value; //数据ID
            selgameversion = document.getElementById('savegameversion').value; //版本
            selgamelable = document.getElementById('savegamelable').value; //版本_标签
            selplatformversionlist = $("#hfPlatformVersionList").val();
            selpluginidlist = $("#hfPlugInIDList").val();

            var urlParam = "?gameid=" + selgameid + "&gamename=" + selgamename + "&gamedisplayname=" + selgamedisplayname + "&gamenamespell=" + selgamenamespell + "&platform=" + selplatform + "&taskid=" + seltaskid + "&gameversion=" + selgameversion + "&gamelable=" + selgamelable;
            //var url = window.location.pathname
            //alert(stridlist); return;
            urlParam = urlParam + "&placeidlist=" + stridlist;
            urlParam = urlParam + "&platformversionlist=" + selplatformversionlist;
            urlParam = urlParam + "&pluginidlist=" + selpluginidlist;
            window.location.href = "./SelectFinalPlace.aspx" + urlParam;

        }

        function back() {
            var selgameid = "";
            var selgamename = "";
            var selgamedisplayname = "";
            var selgamenamespel = "";
            var selplatform = "";
            var seltaskid = "";
            var selgameversion = "";

            selgamename = document.getElementById('savegamename').value; //游戏简称
            selgamedisplayname = document.getElementById('savegamedisplayname').value; //游戏名称
            selgameid = document.getElementById('savegameid').value;     //游戏ID
            selgamenamespell = document.getElementById('savegamenamespell').value;     //游戏全拼
            selplatform = document.getElementById('saveplatform').value; //平台
            seltaskid = document.getElementById('savetaskid').value; //数据ID
            selgameversion = document.getElementById('savegameversion').value; //版本
            var urlParam = "?gameid=" + selgameid + "&gamename=" + selgamename + "&gamedisplayname=" + selgamedisplayname + "&gamenamespell=" + selgamenamespell + "&platform=" + selplatform;

            if (seltaskid != "" && selgameversion != "") {
                urlParam = urlParam + "&taskid=" + seltaskid + "&gameversion=" + selgameversion;
            }
            window.location.href = "./SelectGameVersionList.aspx" + urlParam;
        }


        function GetRadioCheckInfo() {
            var obj = $("input[name='table_records'][type='checkbox']:checked");
            var selplatform = document.getElementById('saveplatform').value; //平台
            var strplacceidList = "";
            var strplatformversionlist = "";
            var strpluginidlist = "";
            for (var i = 0; i < obj.length; i++) {
                var strid = $(obj[i]).val();
                var strplatformversion = $(obj[i]).parent("div").parent("td").parent("tr").children("td").eq(3).text();

                var strpluginid = $(obj[i]).parent("div").parent("td").parent("tr").children("td").eq(3).find("input[type='hidden']").val();
                if (selplatform == "Android")
                    strplacceidList += strid + "','";
                else
                    strplacceidList += strid.substring(0, strid.length - 2) + ",";
                //alert(123);
                strplatformversionlist += strplatformversion.trim() + ",";
                strpluginidlist += strpluginid + ",";
            }
            if (strplacceidList.length > 0) {
                //alert(strplacceidList);
                if (selplatform == "Android")
                    strplacceidList = strplacceidList.substring(0, strplacceidList.length - 3);
                else
                    strplacceidList = strplacceidList.substring(0, strplacceidList.length - 1);
                strplatformversionlist = strplatformversionlist.substring(0, strplatformversionlist.length - 1);
                strpluginidlist = strpluginidlist.substring(0, strpluginidlist.length - 1);
            }
            $("#hfPlatformVersionList").val(strplatformversionlist);
            $("#hfPlugInIDList").val(strpluginidlist);
            //alert(strpluginidlist); return "";
            return strplacceidList;
        }

        //})
    </script>
    <!-- Datatables -->
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
        $('#itemPlaceholderContainer').dataTable({
            "paging": false,
            "order": [[ 2, "asc" ]],
            "orderFixed": [ 4, "asc" ],
            "sPaginationType" : "full_numbers",
            "aoColumnDefs" : [ { "bSortable": false, "aTargets": [0,1,3,4] } ],
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
