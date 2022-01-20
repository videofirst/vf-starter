<!doctype html>
<html lang='en'>
<head>
    <meta charset='UTF-8' />
    <title>Video First Starter - API Docs</title>
    <link rel='icon' type='image/png' href='${r}img/favicon-32x32.png' sizes='32x32' />
    <link rel='icon' type='image/png' href='${r}img/favicon-16x16.png' sizes='16x16' />
    <script src='${r}js/swagger-ui-bundle.js'></script>
    <script src='${r}js/swagger-ui-standalone-preset.js'></script>
    <link rel='stylesheet' type='text/css' href='${r}css/swagger-ui.css' />
    <link rel='stylesheet' type='text/css' href='${r}css/swagger-theme-flattop.css' />
</head>

<body>
    <div id='swagger-ui'></div>
    <script>
        window.onload = function() {
            const extract = function(v) {
                    return decodeURIComponent(v.replace(/(?:(?:^|.*;\s*)contextPath\s*\=\s*([^;]*).*$)|^.*$/, "$1"));
                },
                cookie = extract(document.cookie),
                contextPath = cookie === '' ? extract(window.location.search.substring(1)) : cookie,
                f = contextPath === '' ? undefined : function(system) {
                    return {
                        statePlugins: {
                            spec: {
                                wrapActions: {
                                    updateJsonSpec: (oriAction, system) => (...args) => {
                                        let [spec] = args;
                                        if (spec && spec.paths) {
                                            const newPaths = {};
                                            Object.entries(spec.paths).forEach(([path, value]) => newPaths[contextPath + path] = value);
                                            spec.paths = newPaths;
                                        }
                                        oriAction(...args);
                                    }
                                }
                            }
                        }
                    };
                },
                ui = SwaggerUIBundle({
                    url: contextPath + '/swagger/api.yml',
                    dom_id: '#swagger-ui',
                    presets: [
                        SwaggerUIBundle.presets.apis,
                        SwaggerUIStandalonePreset
                    ],
                    plugins: [
                        SwaggerUIBundle.plugins.DownloadUrl,
                        f
                    ],
                    deepLinking: true,
layout: "StandaloneLayout",
validatorUrl: null
                });
            window.ui = ui;
        };
    </script>
</body>

</html>