# -*- coding: utf-8 -*-
"""
"""

import json
import time

from intelmq.lib import utils
from intelmq.lib.bot import Bot


class CveserverParserBot(Bot):

    def process(self):
        report = self.receive_message()
        self.logger.debug("CveserverParserBot 2.0 starts....")
        raw_report = utils.base64_decode(report.get('raw'))
        report_json = json.loads(raw_report)
        self.logger.debug("CveserverParserBot report_json %s", report_json)
        for item in report_json:
            self.logger.debug("CveserverParserBot item %s", item)
            event = self.new_event(report)
            # add some other remaining fields as extra
            additional = {}            
            misp_event = {"info":"","threat_level_id":"1","attribute_count":"6", "distribution": "1","Object":[{"name":"vulnerability","meta-category":"network","description":"","Attribute":[{"object_relation":"id","type":"vulnerability","value":""},{"type":"text","object_relation":"text","value":""},{"type":"text","object_relation":"summary","value":""},{"type":"text","object_relation":"vulnerable_configuration","value":""},{"type":"link","category":"External analysis","object_relation":"references","value":""},{"type":"text","value":"Published"}]}],"Tag":[{"name":"vulnerability"}]}

            if 'id' in item:
                self.logger.debug("CveserverParserBot item id %s", item['id'])               
                additional['id'] = item['id']   
                misp_event["Object"][0]["Attribute"][0]["value"] = item['id']
                event.add('event_description.target', item['id'])          
            if 'cvss' in item:
                self.logger.debug("CveserverParserBot item cvss %s", item['cvss'])
                additional['cvss'] = item['cvss']     
            if 'cwe' in item:
                additional['cwe'] = item['cwe']     
            if 'Modified' in item:
                additional['Modified'] = item['Modified']   
                #misp_event["timestamp"] = int(time.mktime(time.strptime((item['Modified'])[:19], '%Y-%m-%dT%H:%M:%S'))) - time.timezone  
            if 'Published' in item:
                additional['Published'] = item['Published']
                #misp_event["publish_timestamp"] = int(time.mktime(time.strptime((item['Published'])[:19], '%Y-%m-%dT%H:%M:%S'))) - time.timezone 
            if 'last-modified' in item:
                event.add('time.source', item['last-modified'][:-4] + "+00:00")
            #summary 
            if 'summary' in item:
                event.add('event_description.text', item['summary'][0:20]+ "...")
                misp_event["info"] = item['summary'][0:60]+ "..."
                misp_event["Object"][0]["Attribute"][1]["value"] = item['summary'][0:60]+ "..."
                misp_event["Object"][0]["Attribute"][2]["value"] = item['summary']
               
            #vulnerable_configuration 
            if 'vulnerable_configuration' in item:
                if len(item['vulnerable_configuration']) != 0:
                    self.logger.debug("CveserverParserBot item vulnerable_configuration %s", item['vulnerable_configuration'][0])                    
                    misp_event["Object"][0]["Attribute"][3]["value"] = item['vulnerable_configuration'][0]

            #references 
            if 'references' in item:
                if len(item['references']) != 0:
                    self.logger.debug("CveserverParserBot item reference %s", item['references'][0])
                    event.add('event_description.url', item['references'][0])
                    misp_event["Object"][0]["Attribute"][4]["value"] = item['references'][0]

                                                 
            #misp_event["Event"]["Attribute"][0]["timestamp"] = misp_event["Event"]["timestamp"]
            event.add("extra", additional)                        
            #event.add("raw", json.dumps(item, sort_keys=True))  # sorting for undefined order
            event.add("raw", json.dumps(misp_event, sort_keys=True))  # sorting for undefined order
            self.send_message(event)
		self.logger.debug("CveserverParserBot 2.0 end.")
        self.acknowledge_message()


BOT = CveserverParserBot
