''' Convert a VirusTotal report into MISP objects '''

from pymisp import PyMISP

from viper.common.abstracts import Module
from viper.core.session import __sessions__
# import the necessary packages
# import argparse
from viper.modules.xor import XorSearch
from viper.core.config import Config


class CspXor(Module):
    cmd = 'cspXor'
    description = 'Updates MISP event with a virus total report.'
    authors = ['CSP']

    def __init__(self):
        super(CspXor, self).__init__()

    def run(self):
        if (not __sessions__.is_attached_misp()):
            print('MISP session not attached')
            return

        cfg = Config()
        key = cfg.misp.misp_key
        url = cfg.misp.misp_url


        pymisp = PyMISP(url, key, ssl=False, proxies=None,cert=None)

        xorSearch = XorSearch()
        xorSearch.run()

        event = pymisp.get_event(__sessions__.current.misp_event.event.id)

        for out in xorSearch.output:
            print(str(out))
            if out['type'] == 'error':
                print(">>: " + out['data'])
                pymisp.add_named_attribute(__sessions__.current.misp_event.event.id, "comment", "XOR search out: " + out['data'])




        print(__sessions__.current.file.path)

        files = {'file': (__sessions__.current.file.name, open(__sessions__.current.file.path, 'rb'))}



