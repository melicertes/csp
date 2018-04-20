Set($rtname, 'csp-rt');
Set($WebDomain, '__RT_WEB_DOMAIN__');
Set($WebURL, '__RT_WEB_URL__');

Set($ReferrerWhitelist, qw(__RT_WEB_DOMAIN__:443));

# because we are working behind a proxy :-)
Set($CanonicalizeRedirectURLs, 1);
Set($CanonicalizeURLsInFeeds, 1);

Set($RT_CSP_NAME, '__RT_CSP_NAME__');
Set($RT_TC_MOCK, 'off');
Set($RT_TC_URL, '__RT_TC_URL__');

# ssl communication with emitter
# access url of emitter, e.g.: https://csp-adapter.local.demo1-csp.athens.intrasoft-intl.private:443
Set($RT_EMITTER_URL, '__RT_EMITTER_URL__');
# client certificate key for RT
Set($RT_CLIENT_KEY, '/opt/ssl/server/csp-internal.key');
# client certificate for RT
Set($RT_CLIENT_CRT, '/opt/ssl/server/csp-internal.crt');
# ca certificate for chaining the server certificate
Set($RT_SRV_CA_CRT, '/opt/ssl/ca/common-internal-ca.crt');

# logging (set level debug for more information and restart lighttpd)
# debug info notice warning error critical alert emergency
Set($LogToFile, 'error');
Set($LogDir, '/opt/rt4/var/log');
Set($LogToFileNamed, 'CSP.RT-exc.log');

# setting external authenticate
Set($WebRemoteUserAuth, 1);
# check the user on every request
Set($WebRemoteUserContinuous, 1);
# allow falback solution -> RT login --> no!
Set($WebFallbackToRTLogin, 0);
# do not create not existed users automatically
Set($WebRemoteUserAutocreate, 1);
# session time out, otherwise done by closing the browser
# specifies time in minutes
Set($AutoLoggoff, 15);
Set($MinimumPasswordLength, 0);
Set($WebRemoteUserGecos, 0);

# used PLUGINS
Plugin('RT::IR');

# enabling custom values for trust circles
Set(@CustomFieldValuesSources, "RT::CustomFieldValues::CustomTCSource");

1;
