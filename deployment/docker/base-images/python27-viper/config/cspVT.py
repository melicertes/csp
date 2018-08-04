''' Convert a VirusTotal report into MISP objects '''

from urllib.parse import urlsplit
from pymisp import MISPEvent, PyMISP
from pymisp.mispevent import MISPObject
import pymisp
from viper.common.abstracts import Module
from pymisp.tools import VTReportObject
from viper.core.session import __sessions__
# import the necessary packages
# import argparse
import requests
from viper.core.config import Config



class CspVT(Module):
    cmd = 'cspVT'
    description = 'Updates MISP event with a virus total report.'
    authors = ['CSP']

    def __init__(self):
        super(CspVT, self).__init__()

    def run(self):
        if (not __sessions__.is_attached_misp()):
            self.log("error", "MISP session not attached")
            return

        cfg = Config()
        key = cfg.misp.misp_key
        url = cfg.misp.misp_url

        vt_apikey = cfg.virustotal.virustotal_key

        if vt_apikey == '' or vt_apikey == None:
            self.log("error", 'virustotal_key not set')
            return

        pymisp = PyMISP(url, key, ssl=False, proxies=None, cert=('/opt/ssl/server/csp-internal.crt','/opt/ssl/server/csp-internal.key'))

        # Get VT object template id from misp
        object_pattern_id = 0;
        json_string = pymisp.get_object_templates_list()
        for item in json_string:
            # print("==>" + str(item))
            if str(item["ObjectTemplate"]["name"]) == "virustotal-report":
                # print(item["ObjectTemplate"])
                object_pattern_id = int(item["ObjectTemplate"]["id"])


        url = 'https://www.virustotal.com/vtapi/v2/file/scan'

        params = {'apikey': vt_apikey}

        files = {'file': (__sessions__.current.file.name, open(__sessions__.current.file.path, 'rb'))}

        response = requests.post(url, files=files, params=params)

        indicator = response.json()['md5']

        misp_objects = generate_report(indicator, vt_apikey)

        if (__sessions__.is_attached_misp()):
            self.log("info", 'MISP session attached')
            self.log("info", 'MISP event id: ' + str(__sessions__.current.misp_event.event.id))
            event = pymisp.get_event(__sessions__.current.misp_event.event.id)
            #print(event)
            misp_event = MISPEvent()
            misp_event.load(event)
        else:
            self.log("error", 'MISP session not attached')
            return


        for misp_object in misp_objects:
            res = pymisp.add_object(__sessions__.current.misp_event.event.id, object_pattern_id, misp_object)

def generate_report(indicator, apikey):
    report_objects = []
    vt_report = VTReportObject(apikey, indicator)
    report_objects.append(vt_report)
    raw_report = vt_report._report
    
    file_object = MISPObject(name="file")
    file_object.add_attribute("md5", value=raw_report["md5"])
    file_object.add_attribute("sha1", value=raw_report["sha1"])
    file_object.add_attribute("sha256", value=raw_report["sha256"])
    vt_report.add_reference(referenced_uuid=file_object.uuid, relationship_type="report of")
    report_objects.append(file_object)

    return report_objects

