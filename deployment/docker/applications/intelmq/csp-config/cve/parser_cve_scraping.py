# -*- coding: utf-8 -*-
"""
"""

import json
import time
from bs4 import BeautifulSoup
from intelmq.lib import utils
from intelmq.lib.bot import Bot


class CveScrapingParserBot(Bot):

    def process(self):
        self.logger.info("CveScrapingParserBot starts 11.0.1....")
        report = self.receive_message()
        self.logger.info("CveScrapingParserBot receive_message done.")        
         
        raw_report = utils.base64_decode(report.get('raw'))
        self.logger.info("CveScrapingParserBot base64_decode done.")
        soup = BeautifulSoup(raw_report, "html.parser")        
        self.logger.info("CveScrapingParserBot BeautifulSoup done.")
       
        self.logger.info("CveScrapingParserBot starts soup findAll...")
        for stream_item_tweet in soup.findAll('li', attrs={'data-item-type': 'tweet'}):
            tweet_text = stream_item_tweet.find("div", class_="js-tweet-text-container").p.get_text()
            tweet_textb = tweet_text.encode('utf-8')
            self.logger.info("CveScrapingParserBot encode utf-8 done.")
            tweet_textstr = tweet_textb.decode('utf-8').replace(u"\u00A0", "")
            cve = tweet_textstr.split(" ", 1)[0]
            self.logger.info("CveScrapingParserBot tweet CVE: %s", cve)
            url = tweet_textstr[-22:].strip()
            #self.logger.info("tweet url: %s", url)
            short_tweet_text = tweet_textstr.split(" ", 1)[1][0:70].strip() + " ....."
           
           
            misp_event = {"info":"","threat_level_id":"1","attribute_count":"6", "distribution": "1","Object":[{"name":"vulnerability","meta-category":"network","description":"","Attribute":[{"object_relation":"id","type":"vulnerability","value":""},{"type":"text","object_relation":"text","value":""},{"type":"text","object_relation":"summary","value":""},{"type":"text","object_relation":"vulnerable_configuration","value":""},{"type":"link","category":"External analysis","object_relation":"references","value":""},{"type":"text","value":"Published"}]}],"Tag":[{"name":"vulnerability"}]}

            
            misp_event["Object"][0]["Attribute"][0]["value"] = cve            
            misp_event["info"] = short_tweet_text
           
            misp_event["Object"][0]["Attribute"][1]["value"] = short_tweet_text
            misp_event["Object"][0]["Attribute"][2]["value"] = tweet_textstr
            misp_event["Object"][0]["Attribute"][4]["value"] = url.replace(u"\u00A0", "")
            self.logger.info("CveserverParserBot building misp_event done.")           

            event = self.new_event(report)
            #self.logger.info("CveScrapingParserBot new_event done.")
            event.add('event_description.url', url)
            event.add('event_description.target', cve)
            event.add('event_description.text', short_tweet_text)
            additional = {}
            additional['id'] = cve.strip()
            event.add("extra", additional)
            self.logger.info("CveScrapingParserBot add additional done.")
            event.add("raw", json.dumps(misp_event, sort_keys=True)) 
            self.logger.info("CveScrapingParserBot json.dumps done.")
            self.send_message(event)         
            self.logger.info("CveScrapingParserBot send_message done.")         
        self.logger.info("CveScrapingParserBot 11.0.1 end.")
        self.acknowledge_message()

BOT = CveScrapingParserBot                                       