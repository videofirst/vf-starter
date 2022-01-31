// Set PrismJS grammars path
Prism.plugins.autoloader.languages_path = '/js/prism-grammars/';

/**
 * Update the button state depending on.
 */
function updateCommandsAndButtonStates () {
  var isInvalid = $('input.invalid').length > 0;

  var zipFile = $("#project").val() + ".zip";
  var curlOptions = VF_STARTER_BASE_URL.startsWith("https://localhost") ? "-k " : ""; // https://localhost needs '-k'
  var curlCommand = "curl " + curlOptions + VF_STARTER_BASE_URL + getPathAndParams(true) + " -o " + zipFile;
  var unzipCommand = "unzip " + zipFile + " .";

  // Update button states / commands if validation state is invalid
  if (isInvalid) {
    $('.actBtn').prop('disabled', true);
    $('.actBtn').addClass('disabled');
    $('.command-copy').addClass('dn');
    curlCommand = "";
    unzipCommand = "";
  }
  else{
  	$('.actBtn').prop('disabled', false);
    $('.actBtn').removeClass('disabled');
    $('.command-copy').removeClass('dn');
  }

  $("#curl-command").text(curlCommand);
  $("#unzip-command").text(unzipCommand);
}

/**
 * Generate ZIP file.
 */
function generateZip () {
  var zipUrl = "/" + getPathAndParams(true);
  window.location = zipUrl;
}

/**
 * Generate path using project + ext (zip / json) and parameter list.
 */
function getPathAndParams (isZipFile) {
  var project = $("#project").val();
  var ext = isZipFile ? "zip" : "json";

  var pathAndParams = project + "." + ext + "?prefix=" + $("#prefix").val();
  $('input[data-param="vf-starter"]').each(function() {
    if (this.id !== 'project') {   // ignore project as it is first element in path
      pathAndParams += "&" + this.id + "=" + encodeURIComponent(this.value);
    }
  });

  return pathAndParams;
}

/**
 * Find or create folder (if doesn't exist).
 */
function findOrCreateFolder (parent, folderName) {
  for (var i = 0; i < parent.length; i++) {
    if (parent[i].text === folderName) {
      return parent[i].children; // already exists - so return
    }
  }
  // Create new folder and add to parent
  var folder = {
    'text': folderName,
    'children' : []
  };
  parent.push (folder);
  return folder.children;
}

/**
 * Preview files AJAX call.
 */
function previewFiles() {
  var previewUrl = "/" + getPathAndParams(false);
  $.getJSON(previewUrl, function (data, textStatus, jqXHR) {
    previewData = data;
    initPreviewTree();
  });
}

/**
 * Init preview tree.
 */
function initPreviewTree () {
  var files = Object.keys(previewData.files).sort();

  // Generate tree using files
  var treeData = [];
  for (var i = 0; i < files.length; i++) {
	  var file = files[i];
	  if (file.indexOf('/') === -1) { // normal file (doesn't have forward slash)
  	  treeData.push ({
  	  	'id': file,
  	  	'text': file,
  	  	'icon': 'jstree-file'
  	  });
    }
    else {
      var parts = file.split('/');  // folders have forward slashes, split into their parts
      var folder = treeData; // start at root
      for (p = 0; p < parts.length - 1; p++) {
      	folder = findOrCreateFolder (folder, parts[p]);
      }
      // now add remaining part as a file
      folder.push ({
      	'id': file,
      	'text': parts[parts.length - 1],
      	'icon': 'jstree-file',
      });
    }
  }

  // Check if this is the first time (i.e. preview section isn't visible).
  var displayNone = $('.preview-section').hasClass('dn');
  if (displayNone) {
  	$('.preview-section').removeClass('dn');

   $('.preview-files-tree').jstree({'core' : {'data' : treeData}, 'selectedFilename': previewData.selectedFilename})
    .bind("loaded.jstree", function (event, data) {
      $(this).jstree("open_all");
    })
    .bind("refresh.jstree", function (event, data) {
      $(this).jstree("deselect_all");
      $(this).jstree("open_all");
    })
    .bind("open_all.jstree", function (event, data) {
      var selectedFilename = data.instance.settings.selectedFilename;
      if (selectedFilename) {
        $(this).jstree('select_node', selectedFilename);
      }
    })
    .on("select_node.jstree", function (e, data) {
      var filename = data.node.id;
      var source = getFileContents(filename);
      $(".preview-file-source code").html(source);
      Prism.highlightElement($('.preview-file-source code')[0]);
    });
  }
  else {
    // JSTree already exists so just update data.  The refresh method will signal the bind listeners above ensuring
    // that the node are all opened again and the default filename selected.
    $('.preview-files-tree').jstree(true).settings.core.data = treeData;
    $('.preview-files-tree').jstree(true).settings.selectedFilename = previewData.selectedFilename;
    $('.preview-files-tree').jstree(true).refresh();
  }

  $('#preview-modal').modal('open');
}

/**
 * Return contents of file.
 */
function getFileContents(filename) {
  return previewData.files[filename];
}

/**
 * Copy the curl URL to the clipboard.
 */
function copyText(textId, toastLabel) {
  var textToCopy = $("#" + textId).text();
  copyToClipboard(textToCopy);
  M.toast({html: toastLabel, displayLength: 1000});
}

/**
 * Take values of parameters (any <input> element with data-param="vf-starter") and create a URL request parameters
 *
 * e.g. project=demo&package=com.mycompany&description=test%20description
 */
function getParameterUrlParams () {
  urlParams = "";
  $('input[data-param="vf-starter"]').each(function() {
    if (this.id !== 'project') {   // project is a special case parameter (is also filename)
      urlParams += "&" + this.id + "=" + encodeURIComponent(this.value);
    }
  });
  return urlParams;
}

/**
 * Copy to clip-board.
 */
function copyToClipboard (text) {
  var input = document.body.appendChild(document.createElement("input"));
  input.value = text;
  input.focus();
  input.select();
  document.execCommand('copy');
  input.parentNode.removeChild(input);
}

/**
 * Page load function which will (1) init modal, (2) init Materialize select, (3) add listeners to inputs.
 */
document.addEventListener('DOMContentLoaded', function() {

  // Init Modal
  var instances = M.Modal.init(document.querySelectorAll('.modal'));
  $(".modal").modal({startingTop: '5%', endingTop: '5%'});

  // Init Materialize <select> elements
  $('select').formSelect();

  // Add listeners to <input> elements with (1) empty + (2) regex validation
  $("input").on('input propertychange blur', function(event) {
    var elm = event.currentTarget;
    var val = elm.value;
    var isValid = true; // assume valid

    // check if required field
    if (elm.hasAttribute("required")) {
      isValid = val.trim() !== '';
    }
    // now check for regex
    if (isValid && elm.hasAttribute("regex")) {
      var regex = new RegExp(elm.getAttribute("regex"), 'g');
      isValid = regex.test(val);
    }

    elm.classList.remove(isValid ? "invalid" : "valid");
    elm.classList.add(isValid ? "valid" : "invalid");

    updateCommandsAndButtonStates();
  });

  // Update commands button states
  updateCommandsAndButtonStates();

});
