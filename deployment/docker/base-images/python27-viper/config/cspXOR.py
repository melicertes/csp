''' Convert a VirusTotal report into MISP objects '''

from pymisp import PyMISP

from viper.common.abstracts import Module
from viper.core.session import __sessions__
# import the necessary packages
# import argparse
from viper.modules.xor import XorSearch


class CspXor(Module):
    cmd = 'cspXor'
    description = 'Updates MISP event with a virus total report.'
    authors = ['CSP']

    def __init__(self):
        super(CspXor, self).__init__()

    def run(self):
        # if (not __sessions__.is_attached_misp()):
        #     print('MISP session not attached')
        #     return

        print("Do something.")
        # key = 'gxJGbYKjsJSdQ3IsmfT4dGvwikuwudh2VTig0sb6'
        key = 'RnCpy64iWasEqfwAHTMLy3s5fXxqq38VyXDFOez1'
        # url = 'https://misp.local.demo1-csp.athens.intrasoft-intl.private'
        url = 'http://localhost:8182'

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



