CSP:: Core Service Platform - SMART 2015/1089
--------------------------------------------------------------

The Cyber Security Platform MeliCERTes is part of the European Strategy for Cyber Security. MeliCERTes is a network for establishing confidence and trust among the national Computer Security Incident Response Teams (CSIRTs) of the Member States and for promoting swift and effective operational cooperation. Member States CSIRTs participate on an equal footing in the MeliCERTes Core Service Platform (CSP) within verified Trust Circles for sharing and collaborating on computer security incidents.   

MeliCERTes CSP is a modular platform that interlaces various services that not only offers a complete security incident management solution but also allows CSIRTs to share information and collaborate with each other within verified Trust Circles. Each module specialises in a task essential to security incident management. It uses the following open-source projects: 

- [IntelMQ](https://github.com/certtools/intelmq) harvests and manages security vulnerability events from multiple sources.
- [MISP](https://github.com/MISP/MISP) organises the harvested information as events and is the main module for vulnerability management and information exchange among the CSIRTs.
- [Viper](https://github.com/viper-framework/viper) receives events from MISP for critical malware analysis. The analysis results are updated back into MISP. 
- [OwnCloud](https://github.com/owncloud) is used to securely exchange module files within Trust Circles.
- [Jitsi](https://github.com/jitsi) is for establishing real-time communications channels for quick response and collaboration. 

![CSP Architecture](/Architecture/CSP_Solution_Architecture_4.0.png)

CSP allows CSIRTs to create and participate in multiple Trust Circles. A Trust Circle can be as narrow or wide as is desirable. Each Trust Circle manages with whom information is exchanged. All members within the Trust Circle are verified through security certificates which are managed by a central Registration Authority (RA).

Website / Support
------------------

Checkout the [ENISA website](https://www.enisa.europa.eu/) for more information about the software, standards, tools and communities. 

Documentation
-------------

All of the documentation can be found in this repository in the [documentation folder](https://github.com/melicertes/csp/blob/develop/documentation). There you will find the [CSP User Manual](https://github.com/melicertes/csp/blob/develop/documentation/CSP_User_Manual_v4.0.0.pdf) and [CSP Installation Manual](https://github.com/melicertes/csp/blob/develop/documentation/CSP_Installation_Manual_v4.0.6.pdf)

Contributing
------------

If you are interested in contributing to the CSP framework, please review our [contributing page](CONTRIBUTING.md). Equally important is to read through our [Code of conduct](code_of_conduct.md).

Feel free to fork the code, play with it, make some patches and send us the [pull request](https://github.com/melicertes/csp/pulls).

In case of questions, suggestions, or bugs, feel free to [create an issue](https://github.com/melicertes/csp/issues).

There is one main [master branch](https://github.com/melicertes/csp/tree/master). Features are developed in separated branches and then merged into the master branch.

License
-------

This software is licensed under [EUROPEAN UNION PUBLIC LICENCE v. 1.2 ](https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12)



