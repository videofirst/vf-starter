<#include "global/header.ftl"/>

<!-- Parameter Section  -->
<div class="parameters-section pb-2">

  <p class="content-header pv-1">Welcome to VF Starter - the quickest way to generate <a href="https://github.com/videofirst/vfa"
    target="_blank">VFA (Video First Automation)</a> Projects</p>

  <div class="white-shadow-box row">
     <#list starter.parameters?values as param>
       <#if param.type == 'text'><@parameterText param=param/></#if>
       <#if param.type == 'textarea'><@parameterTextArea param=param/></#if>
     </#list>
     <@parameterIncludePrefix />
  </div>

  <p class="tc pv-1">The project files can be viewed by clicking <b>Preview Files</b> button.
    The <b>Generate Zip</b> button to download your VFA project as a ZIP file.</p>

  <div class="actions-container tc pb-2">
    <span class="waves-effect waves-light actBtn btn mh-0_5" onclick="previewFiles();"><i class="material-icons right">find_in_page</i>Preview Files</span>
    <span class="waves-effect waves-light actBtn btn mh-0_5" onclick="generateZip();"><i class="material-icons right">download</i>Generate ZIP</span>
  </div>

  <div class="curl-commands white-shadow-box w-75 tc">
    <p class="tc">Alternatively, run this <b>curl</b> command to download a generated project directly to a folder on
      your computer.</p>

    <div class="command-container">
      <div id="curl-url" class="command-text"></div>
      <div class="command-copy material-icons" class="material-icons"
           onclick="copyText('copy-curl', 'Curl URL copied to clipboard');">content_copy</div>
    </div>

    <p class="tc">This command will unzip the downloaded project ZIP file.</p>

    <div class="command-container">
      <div id="unzip-command" class="command-text"></div>
      <div class="command-copy material-icons" class="material-icons"
           onclick="copyText('unzip-command', 'Unzip command copied to clipboard');">content_copy</div>
    </div>
  </div>

</div>

<!-- Preview Project Modal  -->
<div id="preview-modal" class="modal">
  <div class="modal-content">
    <h4>Preview Project Files</h4>
    <div class="preview-section dn">
      <div class="preview-files-tree"></div>
      <div class="preview-file-source">
        <pre class="line-numbers"><code class="language-java"></code></pre>
      </div>
    </div>
  </div>
  <div class="modal-footer">
    <a href="#!" class="modal-close waves-effect waves-light btn-flat">Close</a>
  </div>
</div>

<#include "global/footer.ftl"/>