<%@ Page Title="图标管理" Language="C#" MasterPageFile="~/Admin.Master" AutoEventWireup="true" CodeBehind="GameIconList.aspx.cs" Inherits="SDKPackage.GameIcon.GameIconList" %>

<asp:Content ID="Content1" ContentPlaceHolderID="MainContent" runat="server">
    <script type="text/javascript">
        function radioUpdate()
        {
            $("#MainContent_SSFileUpload").removeClass("hidden");
            $("#MainContent_DropDownList2").addClass("hidden");
        }
        function radioSelect() {
            $("#MainContent_SSFileUpload").addClass("hidden");
            $("#MainContent_DropDownList2").removeClass("hidden");
        }
    </script>

    <!--合成图标Model-->
    <div id="composeIcon" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="composeIconLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content ">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                        ×
                    </button>
                    <h2 id="composeIconLabel" class="modal-title">合成ICON组</h2>
                </div>
                <div class="modal-body">
                    <div class="form-horizontal">
                        <div class="form-group">
                            <h4 class="col-md-4 control-label">ICON组名称</h4>
                            <div class="col-md-8">
                                <asp:TextBox ID="TextBox1" runat="server" CssClass="form-control col-md-12"></asp:TextBox>
                            </div>
                        </div>
                        <div class="form-group">
                            <h4 class="col-md-4 control-label">ICON母板</h4>
                            <div class="col-md-8">
                                <asp:DropDownList ID="DropDownListIcon" runat="server" CssClass="form-control" DataSourceID="SqlDataSourceGameIcon" DataTextField="iconName" DataValueField="iconName"></asp:DropDownList>
                            </div>
                        </div>
                        <div class="form-group">
                            <h4 class="col-md-4 control-label">
<%--                                <asp:RadioButtonList ID="RadioButtonList1" runat="server" RepeatDirection="Horizontal">
                                    <asp:ListItem Value="1" Selected="True">上传其他角标</asp:ListItem>
                                    <asp:ListItem Value="2">使用渠道角标</asp:ListItem>
                                </asp:RadioButtonList>--%>
                                <input type="radio" runat="server" id="radio_u" checked="true" onclick="radioUpdate()" value="1" />上传其他角标
                                <input type="radio" runat="server" id="radio_s" onclick="radioSelect()" value="2" />使用渠道角标
                            </h4>
                            <div class="col-md-8" id="colmd1">
                                <asp:FileUpload ID="SSFileUpload" runat="server" CssClass="form-control" />
                                <asp:DropDownList ID="DropDownList2" runat="server" CssClass="form-control hidden" DataSourceID="SqlDataSourcePlatform2" DataTextField="PlatformDisplayName" DataValueField="PlatformIcon"></asp:DropDownList>
                                <asp:SqlDataSource ID="SqlDataSourcePlatform2" runat="server" ConnectionString="<%$ ConnectionStrings:SdkPackageConnString %>" SelectCommand="SELECT PlatformDisplayName,PlatformIcon FROM sdk_DefaultPlatform where PlatformIcon !='' and PlatformIcon is not null" SelectCommandType="Text"></asp:SqlDataSource>
                            </div>
                        </div>
                        <div class="form-group">
                            <br />
                            <h4>如果角标没有预留完整图标空间，只提供了图标内容，需要进行手动尺寸匹配。手动匹配图标强制位置在右下位置</h4>
                            <h4 class="col-md-4 control-label">选择匹配尺寸</h4>
                            <div class="col-md-8">
                                <asp:DropDownList ID="SizeDropDownList" runat="server" CssClass="form-control">
                                    <asp:ListItem Value="0">自动匹配</asp:ListItem>
                                    <asp:ListItem Value="512">512X512</asp:ListItem>
                                    <asp:ListItem Value="144">144X144</asp:ListItem>
                                </asp:DropDownList>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        关闭
                    </button>
                    <asp:Button ID="ButtonComposeIcon" runat="server" class="btn btn-primary" Text="合成" OnClick="ButtonComposeIcon_Click" />
                </div>
            </div>
        </div>
    </div>

    <!--上传图标-->
    <div class="modal fade" id="addIcon" tabindex="-1" role="dialog" aria-labelledby="addIconLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                        ×</button>
                    <h2 class="modal-title" id="addIconLabel">上传图标组
                    </h2>
                </div>
                <div class="modal-body">
                    <div class="form-horizontal">
                        <div class="form-group">
                            <h4 class="col-md-5 control-label">icon名称</h4>
                            <div class="col-md-7">
                                <asp:TextBox ID="IconNameTextBox" runat="server" CssClass="form-control"></asp:TextBox>
                            </div>
                        </div>
                        <div class="form-group">
                            <h4 class="col-md-5 control-label">drawable(512px)</h4>
                            <div class="col-md-7">
                                <asp:FileUpload ID="FileUpload" runat="server" />
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        关闭
                    </button>
                    <asp:Button ID="ButtonAddIcon" runat="server" CssClass="btn btn-primary" Text="保存" OnClick="ButtonAddIcon_Click" />
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
            <div class="x_panel">
                <div class="x_title">
                    <h2>游戏渠道图标管理</h2>
                    <ul class="nav navbar-right panel_toolbox">
                      <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
                      </li>
                    </ul>
                <div class="clearfix"></div>
                </div>
                <div class="x_content">
                    <div class="form-inline">
                        <div class="form-group">
                            <label class="control-label">游戏名称</label>
                            <asp:DropDownList ID="ddlGameList" runat="server" CssClass="form-control" DataSourceID="SqlDataSourceGames" DataTextField="GameDisplayName" DataValueField="GameID" AutoPostBack="True"></asp:DropDownList>
                        </div>
                        <asp:SqlDataSource ID="SqlDataSourceGames" runat="server" ConnectionString="<%$ ConnectionStrings:SdkPackageConnString %>" SelectCommand="sdk_getGameList" SelectCommandType="StoredProcedure">
                            <SelectParameters>
                                <asp:ControlParameter ControlID="saveusername" Type="String" Name="UserName" />
                            </SelectParameters>
                        </asp:SqlDataSource>
                        <asp:HiddenField ID="saveusername" runat="server" />
                        <div class="form-group">
                            <label class="control-label">平台</label>
                            <asp:DropDownList ID="DropDownListSystem" runat="server" CssClass="form-control" AutoPostBack="True">
                                <asp:ListItem Text="Android" Value="1" />
                                <asp:ListItem Text="iOS" Value="2" />
                            </asp:DropDownList>
                        </div>
                        <div class="form-group">
                            <a class="btn btn-primary" data-toggle="modal" href="#addIcon">上传图标组</a>
                            <a class="btn btn-primary" data-toggle="modal" href="#composeIcon">合成图标组</a>
                            <asp:Label ID="MessageLabel" runat="server" CssClass="" Text=""></asp:Label>
                        </div>
                    </div>
                    <hr />

                    <asp:ListView ID="ListView3" runat="server" DataKeyNames="Id,IconName" DataSourceID="SqlDataSourceGameIcon">
                        <EmptyDataTemplate>
                            <div style="font-size: 20px; color: #f00; margin-top: 20px;">未配置</div>
                        </EmptyDataTemplate>
                        <ItemTemplate>
                            <div class="col-md-3 text-center">
                                <div class="thumbnail" style="height: 250px;">
                                    <img data-src="holder.js/150x150?text=<%# Eval("IconName") %>" alt="150x150" src="/icon<%# (DropDownListSystem.SelectedValue == "2") ? "-ios" : "" %>/<%# Eval("GameID") %>/<%# Eval("IconName") %>/app_icon.png"></img>
                                    <label style="text-align:center" class="btn-block"><%# Eval("IconName") %></label>
                                    <br />
                                    <asp:Button style="margin: 0 auto" runat="server" CommandName="Delete" CssClass="btn btn-sm btn-danger fa" Text="&#xf1f8; 删除" OnClientClick="return confirm('确定要删除数据吗？')" ID="DeleteButton" />
                                    <br />
                                </div>
                            </div>
                        </ItemTemplate>
                        <LayoutTemplate>
                                <tr id="itemPlaceholder" runat="server">
                                </tr>
                        </LayoutTemplate>
                    </asp:ListView>
                    <asp:SqlDataSource ID="SqlDataSourceGameIcon" runat="server" ConnectionString="<%$ ConnectionStrings:SdkPackageConnString %>" SelectCommand="sdk_getGameIconList" SelectCommandType="StoredProcedure" 
                        DeleteCommand="delete from sdk_icon where [Id]=@Id" OnDeleted="SqlDataSourceGameIcon_Deleted" >
                        <SelectParameters>
                            <asp:ControlParameter ControlID="ddlGameList" Name="GameID" PropertyName="SelectedValue" Type="String" />
                            <asp:ControlParameter ControlID="DropDownListSystem" Name="SystemID" Type="String" PropertyName="SelectedValue" />
                        </SelectParameters>
                        <DeleteParameters>
                            <asp:Parameter Name="Id" DbType="Int32" />
                            <asp:Parameter Name="IconName" DbType="String"/>
                        </DeleteParameters>
                    </asp:SqlDataSource>
                </div>
            </div>
        </div>
    </div>
</asp:Content>
