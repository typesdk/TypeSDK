<%@ Page Title="游戏渠道参数配置" Language="C#" MasterPageFile="~/Admin.Master" AutoEventWireup="true" CodeBehind="GamePlatformConfig.aspx.cs" Inherits="SDKPackage.GameConfig.GamePlatformConfig" %>

<asp:Content ID="Content1" ContentPlaceHolderID="MainContent" runat="server">
    <!-- Datatables -->
    <link href="/vendors/datatables.net-bs/css/dataTables.bootstrap.min.css" rel="stylesheet">
    <link href="/vendors/datatables.net-buttons-bs/css/buttons.bootstrap.min.css" rel="stylesheet">
    <link href="/vendors/datatables.net-fixedheader-bs/css/fixedHeader.bootstrap.min.css" rel="stylesheet">
    <link href="/vendors/datatables.net-responsive-bs/css/responsive.bootstrap.min.css" rel="stylesheet">
    <link href="/vendors/datatables.net-scroller-bs/css/scroller.bootstrap.min.css" rel="stylesheet">
    

    <style>
        .table {table-layout: fixed;}
        td {
            word-wrap: inherit;
            overflow: hidden;
        }
        th {
            word-break: keep-all;
            overflow: hidden;
        }
        
        #MainContent_lvConfigProduct_ItemIdTextBoxVGR, #MainContent_lvConfigProduct_ItemCpIdTextBoxVGR, #MainContent_lvConfigProduct_PriceTextBoxVGR {
            position: absolute;
        }
    </style>
    <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
            <div class="x_panel">
                <div class="x_title">
                    <h2>游戏渠道参数配置</h2>
                    <ul class="nav navbar-right panel_toolbox">
                      <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
                      </li>
                    </ul>
                <div class="clearfix"></div>
                </div>
                <div class="x_content">
                    <div class="bootstrap-admin-below-content-title">
                        
                        <div class="below-content-title">
                            <span class="name">游戏:</span>
                            <span class="value text-primary"><%= gamedisplayname %></span>
                        </div>
                        <div class="below-content-title">
                            <span class="name">平台:</span>
                            <span class="value text-primary"><%= platformname %></span>
                        </div>
                    </div>

                    <div class="navbar-right" data-toggle="tooltip" data-placement="left" title='打开开发者模式，能够配置更多的TypeSDK参数'>
                            <input id="dev_switch" type="checkbox" class="js-switch" />
                            <span class="value text-primary">开发者模式</span>
                    </div>  
                    <!--保存配置文件-->
                    <br />
                    <p>
                    <a class="btn btn-primary" data-toggle="modal" href="#createConfigFile">
                        <i class='fa fa-save'></i> 将以下设置写入渠道配置文件
                    </a>
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
                    <p/>
                    <p class="alert alert-warning">为保障渠道配置文件的安全，以下参数编辑后并不实际写入文件，请保证参数正确后，点击此按钮保存渠道参数至打包文件。</p>
                    <!--保存配置文件 end-->
                    
                    <!--渠道参数-->
                    <asp:ListView ID="ListView2" runat="server" DataKeyNames="Id" DataSourceID="SqlDataSourcePlatformConfig" InsertItemPosition="LastItem">
                        <EditItemTemplate>
                            <tr>
                                <td>
                                    <asp:Button ID="UpdateButton" runat="server" CommandName="Update" class="btn btn-success btn-sm fa fa-save" Text="&#xf0c7; 更新" />

                                </td>
                                <td data-toggle="tooltip" data-placement="top" title='<%# Eval("SDKKey") %>'>
                                    <asp:Label ID="SDKKeyLabel" runat="server" Text='<%# Eval("SDKKey") %>' />
                                </td>
                                <td>
                                    <asp:TextBox TextMode="MultiLine" width="100%" ID="ExplainTextBox" runat="server" Text='<%# Bind("Explain") %>' />
                                </td>
                                <td>
                                    <asp:TextBox TextMode="MultiLine" width="100%" ID="StringValueTextBox" runat="server" Text='<%# Bind("StringValue") %>' />
                                </td>
                                <td data-toggle="tooltip" data-placement="top" title='用户填写参数'>
                                    <asp:CheckBox ID="isUserCheckBox" runat="server" Checked='<%# Bind("isUser") %>' />
                                </td>
                                <td data-toggle="tooltip" data-placement="top" title='CPSetting'>
                                    <asp:CheckBox ID="isCPSettingCheckBox" runat="server" Checked='<%# Bind("isCPSetting") %>' />
                                </td>
                                <td data-toggle="tooltip" data-placement="top" title='编译参数'>
                                    <asp:CheckBox ID="isBuildingCheckBox" runat="server" Checked='<%# Bind("isBuilding") %>' />
                                </td>
                                <td data-toggle="tooltip" data-placement="top" title='服务端参数'>
                                    <asp:CheckBox ID="isServerCheckBox" runat="server" Checked='<%# Bind("isServer") %>' />
                                </td>
                                <td>
                                    <div style="display:none;">&nbsp;</div></a><asp:Button ID="CancelButton" runat="server" CommandName="Cancel" class="btn btn-primary btn-sm fa fa-reply" Text="&#xf112; 取消" />
                                </td>
                            </tr>
                        </EditItemTemplate>
                        <EmptyDataTemplate>
                            <table>
                                <tr>
                                    <td>未返回数据。</td>
                                </tr>
                            </table>
                        </EmptyDataTemplate>
                        <InsertItemTemplate>
                            <tr>
                                <td>
                                    <asp:Button ID="InsertButton" runat="server" CommandName="Insert" class="btn btn-default btn-sm fa fa-plus" Text="&#xf067; 插入" />
                                </td>
                                <td>
                                    <asp:TextBox ID="SDKKeyTextBox" runat="server" CssClass="form-control" Text='<%# Bind("SDKKey") %>' />
                                </td>
                                <td>
                                    <asp:TextBox ID="ExplainTextBox" runat="server" CssClass="form-control" Text='<%# Bind("Explain") %>' />
                                </td>
                                <td>
                                    <asp:TextBox ID="StringValueTextBox" runat="server" CssClass="form-control" Text='<%# Bind("StringValue") %>' />
                                </td>
                                <td>
                                    <asp:CheckBox ID="isUserCheckBox" runat="server" Checked='<%# Bind("isUser") %>' />
                                </td>
                                <td>
                                    <asp:CheckBox ID="isCPSettingCheckBox" runat="server" Checked='<%# Bind("isCPSetting") %>' />
                                </td>
                                <td>
                                    <asp:CheckBox ID="isBuildingCheckBox" runat="server" Checked='<%# Bind("isBuilding") %>' />
                                </td>
                                <td>
                                    <asp:CheckBox ID="isServerCheckBox" runat="server" Checked='<%# Bind("isServer") %>' />
                                </td>
                                <td>
                                    <asp:Button ID="CancelButton" runat="server" CommandName="Cancel" class="btn btn-default btn-sm fa fa-times" Text="&#xf00d; 清除" />
                                </td>
                            </tr>
                        </InsertItemTemplate>
                        <ItemTemplate>
                            <tr>
                                <td>
                                   <asp:Button ID="EditButton" runat="server" CommandName="Edit" class="btn btn-primary btn-sm fa fa-edit" Text="&#xf044; 编辑" />
                                </td>
                                <td data-toggle="tooltip" data-placement="top" title='<%# Eval("SDKKey") %>'>
                                    <asp:Label ID="SDKKeyLabel" runat="server" Text='<%# Eval("SDKKey") %>' />
                                </td>
                                <td>
                                    <asp:Label ID="ExplainLabel" runat="server" Text='<%# Eval("Explain") %>' />
                                </td>
                                <td>
                                    <asp:Label ToolTip='<%# Eval("StringValue") %>' ID="StringValueLabel" runat="server" Text='<%# Eval("StringValue") %>' />
                                </td>
                                <td data-toggle="tooltip" data-placement="top" title='用户填写参数'>
                                    <%# Eval("isUser").ToString()=="True"?"<div style='display:none;'>&nbsp;</div>":"" %><asp:CheckBox ID="isUserCheckBox" runat="server" Checked='<%# Eval("isUser") %>' Enabled="false" />
                                </td>
                                <td data-toggle="tooltip" data-placement="top" title='CPSetting'>
                                    <asp:CheckBox ID="isCPSettingCheckBox" runat="server" Checked='<%# Eval("isCPSetting") %>' Enabled="false" />
                                </td>
                                <td data-toggle="tooltip" data-placement="top" title='编译参数'>
                                    <asp:CheckBox ID="isBuildingCheckBox" runat="server" Checked='<%# Eval("isBuilding") %>' Enabled="false" />
                                </td>
                                <td data-toggle="tooltip" data-placement="top" title='服务端参数'>
                                    <asp:CheckBox ID="isServerCheckBox" runat="server" Checked='<%# Eval("isServer") %>' Enabled="false" />
                                </td>
                                <td>
                                    <asp:Button ID="DeleteButton" runat="server" CommandName="Delete" class="btn btn-danger btn-sm fa fa-trash" Text="&#xf014; 删除" OnClientClick="return confirm('确定要删除数据吗？')"/>
                                </td>
                            </tr>
                        </ItemTemplate>
                        <LayoutTemplate>
                            <table id="itemPlaceholderContainer" class="table table-striped jambo_table">
                                <col width="80" />
                                <col width="120" />
                                <col width="20%" />
                                <col />
                                <col width="3%" />
                                <col width="3%" />
                                <col width="3%" />
                                <col width="3%" />
                                <col width="80" />
                                <thead>
                                    <tr>
                                        <th>编辑</th>
                                        <th>参数名</th>
                                        <th>说明</th>
                                        <th>参数值</th>
                                        <th>用户填写参数</th>
                                        <th>CPSetting</th>
                                        <th>编译参数</th>
                                        <th>服务端参数</th>
                                        <th>删除</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr id="itemPlaceholder" runat="server">
                                    </tr>
                                </tbody>
                            </table>
                        </LayoutTemplate>
                    </asp:ListView>
                    <asp:SqlDataSource ID="SqlDataSourcePlatformConfig" runat="server" ConnectionString="<%$ ConnectionStrings:SdkPackageConnString %>" DeleteCommand="DELETE FROM [sdk_PlatformConfig] WHERE [Id] = @Id" InsertCommand="INSERT INTO [sdk_PlatformConfig] ([GameName], [PlatformName], [SDKKey], [Explain], [StringValue], [isUser], [isCPSetting], [isBuilding], [isServer],[PlugInID]) VALUES (@GameID, @PlatformID, @SDKKey, @Explain, @StringValue,@isUser, @isCPSetting, @isBuilding, @isServer,@PlugInID)" SelectCommand="[sdk_getGamePlatfromConfig]" SelectCommandType="StoredProcedure" UpdateCommand="UPDATE [sdk_PlatformConfig] SET [Explain] = @Explain, [StringValue] = @StringValue, [isUser] = @isUser, [isCPSetting] = @isCPSetting, [isBuilding] = @isBuilding, [isServer] = @isServer WHERE [Id] = @Id">
                        <DeleteParameters>
                            <asp:Parameter Name="Id" Type="Int32" />
                        </DeleteParameters>
                        <InsertParameters>
                            <asp:QueryStringParameter Name="GameID" QueryStringField="gameid" Type="String" />
                            <asp:QueryStringParameter Name="PlatformID" QueryStringField="platformid" Type="String" />
                            <asp:Parameter Name="SDKKey" Type="String" />
                            <asp:Parameter Name="Explain" Type="String" />
                            <asp:Parameter Name="StringValue" Type="String" />
                            <asp:Parameter Name="isUser" Type="Boolean" />
                            <asp:Parameter Name="isCPSetting" Type="Boolean" />
                            <asp:Parameter Name="isBuilding" Type="Boolean" />
                            <asp:Parameter Name="isServer" Type="Boolean" />
                            <asp:QueryStringParameter Name="PlugInID" QueryStringField="pluginid" Type="Int32" />
                        </InsertParameters>
                        <SelectParameters>
                            <asp:QueryStringParameter Name="GameID" QueryStringField="gameid" Type="String" />
                            <asp:QueryStringParameter Name="PlatformID" QueryStringField="platformid" Type="String" />
                            <asp:QueryStringParameter Name="PlugInID" QueryStringField="pluginid" Type="Int32" />
                        </SelectParameters>
                        <UpdateParameters>
                            <asp:Parameter Name="Explain" Type="String" />
                            <asp:Parameter Name="StringValue" Type="String" />
                            <asp:Parameter Name="isUser" Type="Boolean" />
                            <asp:Parameter Name="isCPSetting" Type="Boolean" />
                            <asp:Parameter Name="isBuilding" Type="Boolean" />
                            <asp:Parameter Name="isServer" Type="Boolean" />
                            <asp:Parameter Name="Id" Type="Int32" />
                        </UpdateParameters>
                    </asp:SqlDataSource>

                    <br />
                    <div class="bootstrap-admin-below-content-title">
                        <div class="below-content-title">
                            <span class="name">商品列表配置</span>
                        </div>
                    </div>
                    
                    <asp:ListView ID="lvConfigProduct" runat="server" DataKeyNames="Id" DataSourceID="dsConfigProduct" InsertItemPosition="LastItem">
                        <EmptyDataTemplate>
                            <table>
                                <tr>
                                    <td>未返回数据。</td>
                                </tr>
                            </table>
                        </EmptyDataTemplate>
                        <ItemTemplate>
                            <tr>
                                <td>
                                    <asp:LinkButton ID="EditButton" runat="server" CommandName="Edit" class="btn btn-primary btn-sm" Text="<i class='fa fa-edit'></i> 编辑" />
                                </td>
                                <td>
                                    <asp:Label ID="ItemIdLabel" runat="server" Text='<%# Eval("itemid") %>' />
                                </td>
                                <td>
                                    <asp:Label ID="ItemCpIdLabel" runat="server" Text='<%# Eval("itemcpid") %>' />
                                </td>
                                <td>
                                    <asp:Label ID="PriceLabel" runat="server" Text='<%# Eval("price") %>' />
                                </td>
                                <td>
                                    <asp:Label ID="NameLabel" runat="server" Text='<%# Eval("name") %>' />
                                </td>
                                <td>
                                    <asp:Label ID="InfoLabel" runat="server" Text='<%# Eval("info") %>' />
                                </td>
                                <td>
                                    <asp:LinkButton ID="DeleteButton" runat="server" CommandName="Delete" class="btn btn-danger btn-sm" Text="<i class='fa fa-trash'></i> 删除" />
                                </td>
                            </tr>
                        </ItemTemplate>
                        <EditItemTemplate>
                            <tr>
                                <td>
                                    <asp:LinkButton ID="UpdateButton" ValidationGroup="myVGUpdate" runat="server" CommandName="Update" class="btn btn-success btn-sm" Text="<i class='fa fa-save'></i> 更新" />
                                </td>
                                <td>
                                    <asp:TextBox ID="ItemIdTextBox" runat="server" Text='<%# Bind("itemid") %>' />
                                    <asp:RequiredFieldValidator ID="ItemIdTextBoxVGR" runat="server"
                                        ErrorMessage="itemid 是必填属性"
                                        ControlToValidate="ItemIdTextBox"
                                        ValidationGroup="myVGUpdate"
                                    />
                                </td>
                                <td>
                                    <asp:TextBox ID="ItemCpIdTextBox" runat="server" Text='<%# Bind("itemcpid") %>' />
                                    <asp:RequiredFieldValidator ID="ItemCpIdTextBoxVGR" runat="server"
                                        ErrorMessage="itemcpid 是必填属性"
                                        ControlToValidate="ItemCpIdTextBox"
                                        ValidationGroup="myVGUpdate"
                                    />
                                </td>
                                <td>
                                    <asp:TextBox ID="PriceTextBox" runat="server" Text='<%# Bind("price") %>' />
                                    <asp:RequiredFieldValidator ID="PriceTextBoxVGR" runat="server"
                                        ErrorMessage="price 是必填属性"
                                        ControlToValidate="PriceTextBox"
                                        ValidationGroup="myVGUpdate"
                                    />
                                </td>
                                <td>
                                    <asp:TextBox ID="NameTextBox" runat="server" Text='<%# Bind("name") %>' />
                                </td>
                                <td>
                                    <asp:TextBox ID="InfoTextBox" runat="server" Text='<%# Bind("info") %>' />
                                </td>
                                <td>
                                    <asp:LinkButton ID="CancelButton" runat="server" CommandName="Cancel" class="btn btn-primary btn-sm" Text="<i class='fa fa-reply'></i> 取消" />
                                </td>
                            </tr>
                        </EditItemTemplate>
                        <InsertItemTemplate>
                            <tr>
                                <td>
                                    <asp:LinkButton ID="InsertButton" ValidationGroup="myVGInsert" runat="server" CommandName="Insert" class="btn btn-default btn-sm" Text="<i class='fa fa-plus'></i> 插入" />
                                </td>
                                <td>
                                    <asp:TextBox ID="ItemIdTextBox" runat="server" CssClass="form-control" Text='<%# Bind("itemid") %>' />
                                    <asp:RequiredFieldValidator ID="ItemIdTextBoxVGR" runat="server"
                                        ErrorMessage="itemid 是必填属性"
                                        ControlToValidate="ItemIdTextBox"
                                        ValidationGroup="myVGInsert"
                                    />
                                </td>
                                <td>
                                    <asp:TextBox ID="ItemCpIdTextBox" runat="server" CssClass="form-control" Text='<%# Bind("itemcpid") %>' />
                                    <asp:RequiredFieldValidator ID="ItemCpIdTextBoxVGR" runat="server"
                                        ErrorMessage="itemcpid 是必填属性"
                                        ControlToValidate="ItemCpIdTextBox"
                                        ValidationGroup="myVGInsert"
                                    />
                                </td>
                                <td>
                                    <asp:TextBox ID="PriceTextBox" runat="server" CssClass="form-control" Text='<%# Bind("price") %>' />
                                    <asp:RequiredFieldValidator ID="PriceTextBoxVGR" runat="server"
                                        ErrorMessage="price 是必填属性"
                                        ControlToValidate="PriceTextBox"
                                        ValidationGroup="myVGInsert"
                                    />
                                </td>
                                <td>
                                    <asp:TextBox ID="NameTextBox" runat="server" CssClass="form-control" Text='<%# Bind("name") %>' />
                                </td>
                                <td>
                                    <asp:TextBox ID="InfoTextBox" runat="server" CssClass="form-control" Text='<%# Bind("info") %>' />
                                </td>
                                <td>
                                    <asp:LinkButton ID="CancelButton" runat="server" CommandName="Cancel" class="btn btn-default btn-sm" Text="<i class='fa fa-times'></i> 清除" />
                                </td>
                            </tr>
                        </InsertItemTemplate>
                        <LayoutTemplate>
                            <table id="itemPlaceholderContainer" class="table table-striped jambo_table">
                                <thead>
                                    <tr>
                                        <th>编辑</th>
                                        <th>渠道商品ID</th>
                                        <th>内部商品ID</th>
                                        <th>价格（单位：分）</th>
                                        <th>商品名</th>
                                        <th>商品说明</th>
                                        <th>删除</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr id="itemPlaceholder" runat="server">
                                    </tr>
                                </tbody>
                            </table>
                        </LayoutTemplate>
                    </asp:ListView>
                    <asp:SqlDataSource ID="dsConfigProduct" runat="server" 
                        ConnectionString="<%$ ConnectionStrings:SdkPackageConnString %>" 
                        DeleteCommand="DELETE FROM [sdk_PlatformConfigProductList] WHERE [Id] = @Id" 
                        InsertCommand="INSERT INTO [sdk_PlatformConfigProductList] ([GameName], [PlatformName], [itemid], [itemcpid], [price], [name], [type], [info]) VALUES (@GameID, @PlatformID, @itemid, @itemcpid, @price, @name, 0, @info)" 
                        SelectCommand="[sdk_getGamePlatfromProductList]" 
                        SelectCommandType="StoredProcedure" 
                        UpdateCommand="UPDATE [sdk_PlatformConfigProductList] SET [itemid] = @itemid, [itemcpid] = @itemcpid, [price] = @price, [name] = @name, [info] = @info WHERE [Id] = @Id">

                        <DeleteParameters>
                            <asp:Parameter Name="Id" Type="Int32" />
                        </DeleteParameters>
                        <InsertParameters>
                            <asp:QueryStringParameter Name="GameID" QueryStringField="gameid" Type="String" />
                            <asp:QueryStringParameter Name="PlatformID" QueryStringField="platformid" Type="String" />
                            <asp:Parameter Name="itemid" Type="String" />
                            <asp:Parameter Name="itemcpid" Type="String" />
                            <asp:Parameter Name="price" Type="Int32" />
                            <asp:Parameter Name="name" Type="String" />
                            <asp:Parameter Name="info" Type="String" />
                        </InsertParameters>
                        <SelectParameters>
                            <asp:QueryStringParameter Name="GameID" QueryStringField="gameid" Type="String" />
                            <asp:QueryStringParameter Name="PlatformID" QueryStringField="platformid" Type="String" />
                        </SelectParameters>
                        <UpdateParameters>
                            <asp:Parameter Name="Id" Type="Int32" />
                            <asp:Parameter Name="itemid" Type="String" />
                            <asp:Parameter Name="itemcpid" Type="String" />
                            <asp:Parameter Name="price" Type="Int32" />
                            <asp:Parameter Name="name" Type="String" />
                            <asp:Parameter Name="info" Type="String" />
                        </UpdateParameters>
                    </asp:SqlDataSource>

                    <hr />
                    <!-- <a class="btn btn-primary" style="margin-left: 20px;" onclick="openWindow('./GamePlatformReplaceKey.aspx?gameid=<%=gameid%>&platformid=<%=platformid%>&gamename=<%=gamename%>&platformname=<%=platformname%>&pluginid=<%=pluginid%>');"><i class='fa fa-edit'></i> 配置编译替换参数</a> -->

                    <!--参数同步至SDK服务端-->
                    <%--<a class="btn btn-primary" style="margin-left: 20px;"  data-toggle="modal" href="#syncServer">
                        <i class='fa fa-edit'></i> 参数同步至SDK服务端
                    </a>

                    <div class="modal fade" id="syncServer" tabindex="-1" role="dialog" aria-labelledby="syncServerLabel" aria-hidden="true">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                                        ×
                                    </button>
                                    <h4 class="modal-title" id="syncServerLabel">确认同步操作
                                    </h4>
                                </div>
                                <div class="modal-body">
                                    同步操作将覆盖SDKServer的Redis内现有配置，是否确认需要覆盖保存。
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-default" data-dismiss="modal">
                                        取消
                                    </button>
                                    <asp:Button ID="ButtonSyncServer" runat="server" class="btn btn-primary" OnClick="ButtonSyncServer_Click" Text="确认保存" />
                                </div>
                            </div>
                        </div>
                    </div>--%>
                    <!--参数同步至SDK服务端 end-->

                    <a class="btn btn-primary navbar-right" style="" href='AddGamePlatfrom.aspx?gameid=<%=gameid %>&androidversionid=<%=androidversionid %>&iosversionid=<%=iosversionid %>&gamedisplayname=<%=gamedisplayname %>'><i class='fa fa-reply'></i> 返回</a>
                    
                    <asp:SqlDataSource ID="SqlDataSourceCpSetting" runat="server" ConnectionString="<%$ ConnectionStrings:SdkPackageConnString %>" SelectCommand="sdk_getPlatformConfigCPSetting" SelectCommandType="StoredProcedure">
                        <SelectParameters>
                            <%--<asp:ControlParameter ControlID="DropDownList1" Name="GameName" PropertyName="SelectedValue" Type="String" />--%>
                            <asp:QueryStringParameter Name="GameName" QueryStringField="gameid" Type="String" />
                            <asp:QueryStringParameter Name="PlatformName" QueryStringField="platformid" Type="String" />
                            <asp:QueryStringParameter Name="PlugInID" QueryStringField="pluginid" Type="Int32" />
                        </SelectParameters>
                    </asp:SqlDataSource>

                    <br />
                    <asp:Label ID="Label2" runat="server" Text=""></asp:Label>
                    <asp:SqlDataSource ID="SqlDataSourceLocal" runat="server" ConnectionString="<%$ ConnectionStrings:SdkPackageConnString %>" SelectCommand="sdk_getPlatformConfigLocal" SelectCommandType="StoredProcedure">
                        <SelectParameters>
                            <%--<asp:ControlParameter ControlID="DropDownList1" Name="GameName" PropertyName="SelectedValue" Type="String" />--%>
                            <asp:QueryStringParameter Name="GameName" QueryStringField="gameid" Type="String" />
                            <asp:QueryStringParameter Name="PlatformName" QueryStringField="platformid" Type="String" />
                            <asp:QueryStringParameter Name="PlugInID" QueryStringField="pluginid" Type="Int32" />
                        </SelectParameters>
                    </asp:SqlDataSource>
                </div>
            </div>
        </div>
    </div>

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
    
    <script type="text/javascript">
        function openWindow(url) {
            var openthiswin = window.open(url, "_bank");
            if (openthiswin != null) openthiswin.focus();
        };
        $('textarea').click( function(){
            if ($(this).val().length > 50) {
                $(this).attr('rows', 10);
            }
        
        });
        $(document).ready(function() {
            var COOKIE_NAME = 'DevSwitch';  
            if( $.cookie(COOKIE_NAME) == "true" ){
               $("#dev_switch").attr("checked",$.cookie(COOKIE_NAME));  
                $("input[id^='MainContent_ListView2_isUserCheckBox_']").closest('tr').show();
             } else {
                $("#dev_switch").removeAttr("checked");  
                $("input[id^='MainContent_ListView2_isUserCheckBox_']").not("input:checked").closest('tr').hide();
            }
        
            $('#itemPlaceholderContainer').dataTable({
                "paging": false,
                "order": [[ 8, "desc" ],[ 4, "desc" ]],
                "sPaginationType" : "full_numbers",
                "aoColumnDefs" : [ { "bSortable": false, "aTargets": [0,3,4,5,6,7,8] } ],
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
            
            $("#dev_switch").change(function (){
                    if ($(this).is(':checked')) {
                        $.cookie(COOKIE_NAME, $("#dev_switch").prop("checked") , { path: '/', expires: 300 }); 
                        $("input[id^='MainContent_ListView2_isUserCheckBox_']").closest('tr').show();
                    } else {
                        $.cookie(COOKIE_NAME, $("#dev_switch").prop("checked") , { path: '/', expires: 300 }); 
                        $("input[id^='MainContent_ListView2_isUserCheckBox_']").not("input:checked").closest('tr').hide();
                    }
                }
            
            );
        });
    </script>
</asp:Content>
