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



class CspVT(Module):
    cmd = 'cspVT'
    description = 'This module does this and that'
    authors = ['CSP']

    def __init__(self):
        super(CspVT, self).__init__()

    def run(self):
        if (not __sessions__.is_attached_misp()):
            print('MISP session not attached')
            return

        print("Do something.")
        key = 'RnCpy64iWasEqfwAHTMLy3s5fXxqq38VyXDFOez1'
        url = 'http://localhost:8182'

        vt_apikey = '56e0213297540537b9dad11f0e28957b16706a7038363afcc8a3f0db1eb07e10'

        pymisp = PyMISP(url, key, ssl=False, proxies=None, cert=None)

        print(__sessions__.current.file.path)

        url = 'https://www.virustotal.com/vtapi/v2/file/scan'

        params = {'apikey': vt_apikey}

        files = {'file': (__sessions__.current.file.name, open(__sessions__.current.file.path, 'rb'))}

        response = requests.post(url, files=files, params=params)

        print(response.json())

        indicator = response.json()['md5']

        misp_objects = generate_report(indicator, vt_apikey)
        print(misp_objects)

        if (__sessions__.is_attached_misp()):
            print('MISP session attached')
            print ('MISP event id: ' + str(__sessions__.current.misp_event.event.id))
            event = pymisp.get_event(__sessions__.current.misp_event.event.id)
            print(event)
            misp_event = MISPEvent()
            misp_event.load(event)
        else:
            print('MISP session not attached')
            return

        for misp_object in misp_objects:
            print(misp_object)
            pymisp.add_object(misp_event.id, 41, misp_object)


def generate_report(indicator, apikey):
    '''
    Build our VirusTotal report object, File object, and AV signature objects
    and link them appropriately
    :indicator: Indicator hash to search in VT for
    '''
    report_objects = []
    vt_report = VTReportObject(apikey, indicator)
    report_objects.append(vt_report)
    raw_report = vt_report._report

    vt_report._resource_type = "file"

    if vt_report._resource_type == "file":
        file_object = MISPObject(name="file")
        file_object.add_attribute("md5", value=raw_report["md5"])
        file_object.add_attribute("sha1", value=raw_report["sha1"])
        file_object.add_attribute("sha256", value=raw_report["sha256"])
        vt_report.add_reference(referenced_uuid=file_object.uuid, relationship_type="report of")
        report_objects.append(file_object)
    elif vt_report._resource_type == "url":
        parsed = urlsplit(indicator)
        url_object = pymisp.MISPObject(name="url")
        url_object.add_attribute("url", value=parsed.geturl())
        url_object.add_attribute("host", value=parsed.hostname)
        url_object.add_attribute("scheme", value=parsed.scheme)
        url_object.add_attribute("port", value=parsed.port)
        vt_report.add_reference(referenced_uuid=url_object.uuid, relationship_type="report of")
        report_objects.append(url_object)
    for antivirus in raw_report["scans"]:
        if raw_report["scans"][antivirus]["detected"]:
            av_object = pymisp.MISPObject(name="av-signature")
            av_object.add_attribute("software", value=antivirus)
            signature_name = raw_report["scans"][antivirus]["result"]
            av_object.add_attribute("signature", value=signature_name, disable_correlation=True)
            vt_report.add_reference(referenced_uuid=av_object.uuid, relationship_type="included-in")
            report_objects.append(av_object)
    return report_objects


