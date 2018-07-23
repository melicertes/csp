''' Convert a VirusTotal report into MISP objects '''

from pymisp import PyMISP

from viper.common.abstracts import Module
from viper.core.session import __sessions__
# import the necessary packages
# import argparse
from viper.modules.xor import XorSearch
from viper.core.config import Config
from viper.modules.shellcode import  Shellcode



class CspXor(Module):
    cmd = 'cspShellcode'
    description = 'Updates MISP event with the shellcode analysis results.'
    authors = ['CSP']

    def __init__(self):
        super(CspXor, self).__init__()

    def run(self):
        if (not __sessions__.is_attached_misp()):
            self.log("error", "MISP session not attached")
            return

        cfg = Config()
        key = cfg.misp.misp_key
        url = cfg.misp.misp_url

        pymisp = PyMISP(url, key, ssl=False, proxies=None,cert=None)

        xorSearch = Shellcode()
        xorSearch.run()

        commentVal = ""
        for out in xorSearch.output:
            commentVal += out['data']

        self.log("info", "Updating MISP event " + str(__sessions__.current.misp_event.event.id) + "...")
        pymisp.add_named_attribute(__sessions__.current.misp_event.event.id, "comment", "File: " + __sessions__.current.file.path + " -- Shellcode out: " + commentVal)
