<%@include file="../init.jsp"%>

<liferay-ui:success key="entryAdded" message="entry-added" />
<liferay-ui:success key="entryDeleted" message="entry-deleted" />

<%
long guestbookId = Long.valueOf((Long) renderRequest.getAttribute("guestbookId"));
%>

<portlet:renderURL var="searchURL">
    <portlet:param name="mvcPath" value="/guestbook/view_search.jsp" />
</portlet:renderURL>

<aui:form action="${searchURL}" name="fm">

    <div class="row">
        <div class="col-md-8">
            <aui:input inlineLabel="left" label="" name="keywords" placeholder="search-entries" size="256" />
        </div>

        <div class="col-md-4">
            <aui:button type="submit" value="search" />
        </div>
    </div>

 </aui:form>

<aui:nav cssClass="nav-tabs">

    <%
    List<Guestbook> guestbooks = GuestbookLocalServiceUtil.getGuestbooks(scopeGroupId, WorkflowConstants.STATUS_APPROVED);

            for (int i = 0; i < guestbooks.size(); i++) {

                Guestbook curGuestbook = guestbooks.get(i);
                String cssClass = StringPool.BLANK;

                if (curGuestbook.getGuestbookId() == guestbookId) {
                    cssClass = "active";
                }

                if (GuestbookModelPermission.contains(
                    permissionChecker, curGuestbook.getGuestbookId(), "VIEW")) {

    %>

    <portlet:renderURL var="viewPageURL">
        <portlet:param name="mvcPath" value="/guestbook/view.jsp" />
        <portlet:param name="guestbookId" value="<%=String.valueOf(curGuestbook.getGuestbookId())%>" />
    </portlet:renderURL>


    <aui:nav-item cssClass="<%=cssClass%>" href="<%=viewPageURL%>" label="<%=HtmlUtil.escape(curGuestbook.getName())%>" />

    <%  
                }

            }
    %>

</aui:nav>




<aui:button-row cssClass="guestbook-buttons">

    <c:if test='<%= GuestbookPermission.contains(permissionChecker, scopeGroupId, "ADD_ENTRY") %>'>

    <portlet:renderURL var="addEntryURL">
        <portlet:param name="mvcPath" value="/guestbook/edit_entry.jsp" />
        <portlet:param name="guestbookId" value="<%=String.valueOf(guestbookId)%>" />
    </portlet:renderURL>

    <aui:button onClick="<%=addEntryURL.toString()%>" value="Add Entry"></aui:button>
    
    </c:if>

</aui:button-row>

<portlet:resourceURL var="exportExcel" id="exportExcel"></portlet:resourceURL>

<liferay-ui:search-container total="<%=GuestbookEntryLocalServiceUtil.getGuestbookEntriesCount(scopeGroupId.longValue(), guestbookId, WorkflowConstants.STATUS_APPROVED)%>" delta="5">

<liferay-ui:search-container-results results="<%=GuestbookEntryLocalServiceUtil.getGuestbookEntries(scopeGroupId.longValue(), guestbookId, WorkflowConstants.STATUS_APPROVED, searchContainer.getStart(), searchContainer.getEnd())%>" />

<!--<div class="dt-buttons"><a href="<%=exportExcel%>" class="dt-button buttons-excel buttons-html5" style="float:right"><span><liferay-ui:message key="Excel Download"/></span></a></div>-->

<div class="dt-buttons">
  <a href="<%= exportExcel %>" class="dt-button buttons-excel buttons-html5" style="float: right; background-color: #ff0000; color: #fff; padding: 10px 20px; text-decoration: none; border-radius: 5px; font-weight: bold;">
    <span><liferay-ui:message key="Excel"/></span>
  </a>
</div>

<br>
<br>

<liferay-ui:search-container-row className="com.liferay.docs.guestbook.model.GuestbookEntry" modelVar="entry">

    <liferay-ui:search-container-column-text property="name" />

    <liferay-ui:search-container-column-text property="message" />  
    
    <liferay-ui:search-container-column-text property="email" />
    
    <liferay-ui:search-container-column-text property="createDate" />

    <liferay-ui:search-container-column-jsp  align="right" path="/guestbook/entry_actions.jsp" />

</liferay-ui:search-container-row>

<liferay-ui:search-iterator markupView="lexicon"/>

</liferay-ui:search-container>

