<%@ Page Title="游戏渠道配置" Language="C#" MasterPageFile="~/Admin.Master" AutoEventWireup="true" CodeBehind="GamePlatformIcon.aspx.cs" Inherits="SDKPackage.GameConfig.GamePlatformIcon" %>

<asp:Content ID="Content1" ContentPlaceHolderID="MainContent" runat="server">
    <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
            <div class="x_panel">
                <div class="x_title">
                    <h2>游戏渠道Icon配置</h2>
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
                            <span class="value text-primary"><%= gamedisplayname %></span>
                        </div>
                        <div>
                            <span class="name">渠道:</span>
                            <span class="value text-primary"><%= platformname %></span>
                        </div>
                        <div>
                            <span class="name">当前图标:</span>
                            <asp:Label ID="labelCurrIcon" runat="server" CssClass="value text-primary"></asp:Label>
                        </div>
                    </div>
                    <hr />
                    <asp:ListView ID="ListView1" runat="server" DataSourceID="SqlDataSourceIcon" DataKeyNames="Id">
                        <EditItemTemplate>
                            <div class="col-md-3 text-center">
                                <div class="thumbnail" style="height:250px;">
                                    <img data-src="holder.js/150x150?text=<%# Eval("IconName") %>" alt="150x150" style="width: 150px; height: 150px; margin-top:10px"" src="/icon<%# (systemname == "2") ? "-ios" : "" %>/<%# Eval("GameID") %>/<%# Eval("IconName") %>/app_icon.png"></img>
                                    <%# Eval("IconName") %>
                                    <br /><br />
                                    <div class="row">
                                        <div class="col-xs-6 text-center">
                                            <asp:Button runat="server" CommandName="Update" CssClass="btn btn-sm btn-primary" Text="确认" ID="UpdateButton" />
                                        </div>
                                        <div class="col-xs-6 text-center">
                                            <asp:Button runat="server" CommandName="Cancel" CssClass="btn btn-sm btn-default" Text="取消" ID="CancelButton" />
                                        </div>
                                    </div>
                                </div>
                                    
                                    <asp:Label CssClass="hidden" Text='<%# Bind("IconName") %>' runat="server" ID="IconName" />
                                    <asp:Label CssClass="hidden" Text='<%# Bind("GameID") %>' runat="server" ID="GameID" />
                                
                            </div>
                        </EditItemTemplate>
                        <EmptyDataTemplate>
                            <div>
                                未返回数据。
                            </div>
                        </EmptyDataTemplate>
                        <ItemTemplate>
                            <div class="col-md-3 text-center">
                                <div class="thumbnail" style="height:250px;">
                                    <img data-src="holder.js/150x150?text=<%# Eval("IconName") %>" alt="150x150" style="width: 150px; height: 150px; margin-top:10px"" src="/icon<%# (systemname == "2") ? "-ios" : "" %>/<%# Eval("GameID") %>/<%# Eval("IconName") %>/app_icon.png"></img>
                                <%# Eval("IconName") %>
                                <br /><br />
                                <asp:Button runat="server" CommandName="Edit" CssClass="btn btn-sm btn-primary" Text="选择此图标" ID="EditButton" />
                                </div>
                                </div>
                        </ItemTemplate>
                        <LayoutTemplate>
                            <tr runat="server" id="itemPlaceholder"></tr>
                        </LayoutTemplate>
                    </asp:ListView>
                    <asp:SqlDataSource runat="server" ID="SqlDataSourceIcon" ConnectionString='<%$ ConnectionStrings:SdkPackageConnString %>' SelectCommand="sdk_getGameIconList" SelectCommandType="StoredProcedure" UpdateCommand="exec sdk_setPlatformGameIcon @IconName, @GameID, @PlatformID">
                        <SelectParameters>
                            <asp:QueryStringParameter QueryStringField="gameid" Name="GameID" Type="String"></asp:QueryStringParameter>
                            <asp:QueryStringParameter QueryStringField="systemid" Name="SystemID" Type="Int32"></asp:QueryStringParameter>
                        </SelectParameters>
                        <UpdateParameters>
                            <asp:Parameter Name="IconName" Type="String"></asp:Parameter>
                            <asp:Parameter Name="GameID" Type="Int32"></asp:Parameter>
                            <asp:QueryStringParameter QueryStringField="platformid" Name="PlatformID" Type="Int32"></asp:QueryStringParameter>
                        </UpdateParameters>
                    </asp:SqlDataSource>
                </div>
            </div>
        <div class="text-center">
                <a class="btn btn-default btn-sm" style="" href='AddGamePlatfrom.aspx?gameid=<%=gameid %>&androidversionid=<%=androidversionid %>&iosversionid=<%=iosversionid %>&gamedisplayname=<%=gamedisplayname %>'><i class='fa fa-reply'></i> 返回</a>
                </div>
        </div>
    </div>
</asp:Content>
