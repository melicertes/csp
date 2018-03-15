/* jshint maxlen:false */

var config = { // eslint-disable-line no-unused-vars
    hosts: {
        domain: '{{ DOMAIN }}',
        muc: 'conference.{{ DOMAIN }}', // FIXME: use XEP-0030
        focus: 'focus.{{ DOMAIN }}', // defaults to 'focus.ok'
        bridge: 'jitsi-videobridge.{{ DOMAIN }}',
    },
    useNicks: false,
    bosh: '//{{ DOMAIN }}/http-bind', // FIXME: use xep-0156 for that
    clientNode: 'http://jitsi.org/jitsimeet', // The name of client node advertised in XEP-0115 'c' stanza
};
