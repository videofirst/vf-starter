<#include "macros.ftl">
<!DOCTYPE html>
<html lang="en">
<head>
  <title>Video First - Starter</title>

  <link rel="icon" href="${r}favicon.ico" type="image/x-icon" />
  <link rel="shortcut icon" href="${r}favicon.ico" type="image/x-icon" />

  <!-- CSS -->

  <link rel="stylesheet" href="${r}css/material-icons.css"/>

  <link rel="stylesheet" href="${r}css/materialize-1.0.0.min.css"/>

  <link rel="stylesheet" href="${r}css/jstree-3.2.1.min.css"/>

  <link rel="stylesheet" href="${r}css/prism-1.25.0.min.css"/>
  <link rel="stylesheet" href="${r}css/prism-line-numbers-1.25.0.min.css"/>

  <link rel="stylesheet" href="${r}css/vf-starter.css"/>

  <!-- JavaScript -->

  <script type="text/javascript">
    // Dynamically set global variables which JavaScript functions can use
    VF_STARTER_BASE_URL="${config.baseUrl}/"
  </script>

  <script src="${r}js/jquery-3.1.1.min.js"></script>

  <script src="${r}js/materialize-1.0.0.min.js"></script>

  <script src="${r}js/jstree-3.2.1.min.js"></script>

  <script src="${r}js/prism-1.25.0.min.js"></script>
  <script src="${r}js/prism-autoloader-1.25.0.min.js"></script>
  <script src="${r}js/prism-line-numbers-1.25.0.min.js"></script>
  <script src="${r}js/prism-line-numbers-1.25.0.min.js"></script>

  <script src="${r}js/vf-starter.js"></script>
</head>
<body>

<div class="header">
  <div class="header-content">
    <a href="https://www.videofirst.io" target="_blank">
      <div class="header-title">
        <div class="header-image"><img src="${r}img/video-first-light.svg"/></div>
        <div class="header-text">Starter</div>
      </div>
    </a>
    <div class="header-sub-title">
      Generate VFA Projects
    </div>
    <div class="header-icons">
      <a href="/api-docs" target="_blank" title="API Documentation" rel="noopener noreferrer">
        <img class="header-icon" src="${r}img/icon-swagger.svg"/>
      </a>
      <a href="https://github.com/videofirst/vf-starter" target="_blank" title="Github Page" rel="noopener noreferrer">
        <img class="header-icon" src="${r}img/icon-github.svg"/>
      </a>
      <a href="https://twitter.com/video_first_io" target="_blank" title="Twitter - Video First" rel="noopener noreferrer">
        <img class="header-icon" src="${r}img/icon-twitter.svg"/>
      </a>
      <a href="mailto:info@videofirst.io" target="_blank" title="Email the Video First Team" rel="noopener noreferrer">
        <img class="header-icon" src="${r}img/icon-email.svg" />
      </a>
    </div>
  </div>
</div>

<div class="main">