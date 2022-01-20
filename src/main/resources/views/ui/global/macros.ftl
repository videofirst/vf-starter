<#--
====================================================================================================
PARAMETER MACROS
====================================================================================================
-->

<#macro parameterText param>
  <@parameterDiv param=param>
    <input id="${param.id}" type="text" class="validate" value="${param.value}" data-param="vf-starter"
       <#if param.validation?? && param.validation.match??> regex="${param.validation.match}"</#if>
       <#if param.validation?? && param.validation.required??> required="" aria-required="true"</#if>
       / >
    <label for="project">${param.name}</label>
    <#if param.validation?? && param.validation.message??><span class="helper-text"
          data-error="${param.validation.message}"></span></#if>
  </@parameterDiv>
</#macro>

<#macro parameterDiv param>
  <div class="input-field col ${param.size}">
    <#nested>
  </div>
</#macro>

<#macro parameterTextArea param>
  <@parameterDiv param=param>
    <textarea>TODO</textarea>
  </@parameterDiv>
</#macro>

<#macro parameterIncludePrefix size="s2">
  <div class="input-field col ${size}">
    <select id="prefix" class="initialized" onchange="updateCommandsAndButtonStates();">
      <option value="true">Yes</option>
      <option value="false">No</option>
    </select>
    <label for="prefix">Add root folder in ZIP</label>
    <span class="helper-text" data-error="">Select "No" if folder already exists (e.g. GIT repo)</span>
  </div>
</#macro>