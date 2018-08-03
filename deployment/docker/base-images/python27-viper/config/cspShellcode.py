from pymisp import PyMISP

from viper.common.abstracts import Module
from viper.core.session import __sessions__
# import the necessary packages
# import argparse
from viper.modules.xor import XorSearch
from viper.core.config import Config
from viper.modules.shellcode import  Shellcode



class CspShellcode(Module):
    cmd = 'cspShellcode'
    description = 'Updates MISP event with a virus total report.'
    authors = ['CSP']

    def __init__(self):
        super(CspShellcode, self).__init__()

    def run(self):
        if (not __sessions__.is_attached_misp()):
            self.log("error", "MISP session not attached")
            return

        cfg = Config()
        key = cfg.misp.misp_key
        url = cfg.misp.misp_url

        pymisp = PyMISP(url, key, ssl=False, proxies=None, cert=('/opt/ssl/server/csp-internal.crt','/opt/ssl/server/csp-internal.key'))

        shellcode = Shellcode()
        shellcode.run()

        commentVal = ""
        for out in shellcode.output:
            commentVal += out['data']

        self.log("info", "Updating MISP event " + str(__sessions__.current.misp_event.event.id) + "...")
        pymisp.add_named_attribute(__sessions__.current.misp_event.event.id, "comment", "Shellcode out: " + commentVal)
