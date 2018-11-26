#!/usr/bin/env bash


  echo ""
  echo ""
  echo " Settings CONFIGURATION"
  echo ""
  echo ""
  echo ""

curl -XPUT  'localhost:9200/cspdata?pretty' -H 'Content-Type: application/json' -d'
{
    "settings" : {
    }
}
'

  echo ""
  echo ""
  echo " artefact CONFIGURATION"
  echo ""
  echo ""
  echo ""

curl -XPUT  'localhost:9200/cspdata/_mapping/artefact?pretty' -H 'Content-Type: application/json' -d'
{
        "properties" : {
          "dataObject" : {
            "properties" : {
              "analysis" : {
                "properties" : {
                  "module" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "report" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  }
                }
              },
              "default" : {
                "properties" : {
                  "crc32" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "created_at" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "id" : {
                    "type" : "long"
                  },
                  "md5" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "name" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "sha1" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "sha256" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "sha512" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "size" : {
                    "type" : "long"
                  },
                  "ssdeep" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "tags" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "type" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  }
                }
              }
            }
          },
          "dataParams" : {
            "properties" : {
              "applicationId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "cspId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "dateTime" : {
                "type" : "date"
              },
              "originApplicationId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "originCspId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "originRecordId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "recordId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "url" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              }
            }
          },
          "dataType" : {
            "type" : "text",
            "fields" : {
              "keyword" : {
                "type" : "keyword",
                "ignore_above" : 256
              }
            }
          },
          "sharingParams" : {
            "properties" : {
              "isExternal" : {
                "type" : "boolean"
              },
              "trustCircleId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "teamId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "toShare" : {
                "type" : "boolean"
              }
            }
          }
        }
}
'

  echo ""
  echo ""
  echo " vulnerability CONFIGURATION"
  echo ""
  echo ""
  echo ""

curl -XPUT  'localhost:9200/cspdata/_mapping/vulnerability?pretty' -H 'Content-Type: application/json' -d'
{
        "properties" : {
          "dataObject" : {
            "properties" : {
              "response" : {
                "properties" : {
                  "Event" : {
                    "properties" : {
                      "Attribute" : {
                        "properties" : {
                          "category" : {
                            "type" : "text",
                            "fields" : {
                              "keyword" : {
                                "type" : "keyword",
                                "ignore_above" : 256
                              }
                            }
                          },
                          "comment" : {
                            "type" : "text",
                            "fields" : {
                              "keyword" : {
                                "type" : "keyword",
                                "ignore_above" : 256
                              }
                            }
                          },
                          "deleted" : {
                            "type" : "boolean"
                          },
                          "disable_correlation" : {
                            "type" : "boolean"
                          },
                          "distribution" : {
                            "type" : "text",
                            "fields" : {
                              "keyword" : {
                                "type" : "keyword",
                                "ignore_above" : 256
                              }
                            }
                          },
                          "event_id" : {
                            "type" : "text",
                            "fields" : {
                              "keyword" : {
                                "type" : "keyword",
                                "ignore_above" : 256
                              }
                            }
                          },
                          "id" : {
                            "type" : "text",
                            "fields" : {
                              "keyword" : {
                                "type" : "keyword",
                                "ignore_above" : 256
                              }
                            }
                          },
                          "sharing_group_id" : {
                            "type" : "text",
                            "fields" : {
                              "keyword" : {
                                "type" : "keyword",
                                "ignore_above" : 256
                              }
                            }
                          },
                          "timestamp" : {
                            "type" : "text",
                            "fields" : {
                              "keyword" : {
                                "type" : "keyword",
                                "ignore_above" : 256
                              }
                            }
                          },
                          "to_ids" : {
                            "type" : "boolean"
                          },
                          "type" : {
                            "type" : "text",
                            "fields" : {
                              "keyword" : {
                                "type" : "keyword",
                                "ignore_above" : 256
                              }
                            }
                          },
                          "uuid" : {
                            "type" : "text",
                            "fields" : {
                              "keyword" : {
                                "type" : "keyword",
                                "ignore_above" : 256
                              }
                            }
                          },
                          "value" : {
                            "type" : "text",
                            "fields" : {
                              "keyword" : {
                                "type" : "keyword",
                                "ignore_above" : 256
                              }
                            }
                          }
                        }
                      },
                      "Org" : {
                        "properties" : {
                          "id" : {
                            "type" : "text",
                            "fields" : {
                              "keyword" : {
                                "type" : "keyword",
                                "ignore_above" : 256
                              }
                            }
                          },
                          "name" : {
                            "type" : "text",
                            "fields" : {
                              "keyword" : {
                                "type" : "keyword",
                                "ignore_above" : 256
                              }
                            }
                          },
                          "uuid" : {
                            "type" : "text",
                            "fields" : {
                              "keyword" : {
                                "type" : "keyword",
                                "ignore_above" : 256
                              }
                            }
                          }
                        }
                      },
                      "Orgc" : {
                        "properties" : {
                          "id" : {
                            "type" : "text",
                            "fields" : {
                              "keyword" : {
                                "type" : "keyword",
                                "ignore_above" : 256
                              }
                            }
                          },
                          "name" : {
                            "type" : "text",
                            "fields" : {
                              "keyword" : {
                                "type" : "keyword",
                                "ignore_above" : 256
                              }
                            }
                          },
                          "uuid" : {
                            "type" : "text",
                            "fields" : {
                              "keyword" : {
                                "type" : "keyword",
                                "ignore_above" : 256
                              }
                            }
                          }
                        }
                      },
                      "RelatedEvent" : {
                        "properties" : {
                          "Event" : {
                            "properties" : {
                              "Org" : {
                                "properties" : {
                                  "id" : {
                                    "type" : "text",
                                    "fields" : {
                                      "keyword" : {
                                        "type" : "keyword",
                                        "ignore_above" : 256
                                      }
                                    }
                                  },
                                  "name" : {
                                    "type" : "text",
                                    "fields" : {
                                      "keyword" : {
                                        "type" : "keyword",
                                        "ignore_above" : 256
                                      }
                                    }
                                  },
                                  "uuid" : {
                                    "type" : "text",
                                    "fields" : {
                                      "keyword" : {
                                        "type" : "keyword",
                                        "ignore_above" : 256
                                      }
                                    }
                                  }
                                }
                              },
                              "Orgc" : {
                                "properties" : {
                                  "id" : {
                                    "type" : "text",
                                    "fields" : {
                                      "keyword" : {
                                        "type" : "keyword",
                                        "ignore_above" : 256
                                      }
                                    }
                                  },
                                  "name" : {
                                    "type" : "text",
                                    "fields" : {
                                      "keyword" : {
                                        "type" : "keyword",
                                        "ignore_above" : 256
                                      }
                                    }
                                  },
                                  "uuid" : {
                                    "type" : "text",
                                    "fields" : {
                                      "keyword" : {
                                        "type" : "keyword",
                                        "ignore_above" : 256
                                      }
                                    }
                                  }
                                }
                              },
                              "analysis" : {
                                "type" : "text",
                                "fields" : {
                                  "keyword" : {
                                    "type" : "keyword",
                                    "ignore_above" : 256
                                  }
                                }
                              },
                              "date" : {
                                "type" : "date"
                              },
                              "distribution" : {
                                "type" : "text",
                                "fields" : {
                                  "keyword" : {
                                    "type" : "keyword",
                                    "ignore_above" : 256
                                  }
                                }
                              },
                              "id" : {
                                "type" : "text",
                                "fields" : {
                                  "keyword" : {
                                    "type" : "keyword",
                                    "ignore_above" : 256
                                  }
                                }
                              },
                              "info" : {
                                "type" : "text",
                                "fields" : {
                                  "keyword" : {
                                    "type" : "keyword",
                                    "ignore_above" : 256
                                  }
                                }
                              },
                              "org_id" : {
                                "type" : "text",
                                "fields" : {
                                  "keyword" : {
                                    "type" : "keyword",
                                    "ignore_above" : 256
                                  }
                                }
                              },
                              "orgc_id" : {
                                "type" : "text",
                                "fields" : {
                                  "keyword" : {
                                    "type" : "keyword",
                                    "ignore_above" : 256
                                  }
                                }
                              },
                              "published" : {
                                "type" : "boolean"
                              },
                              "threat_level_id" : {
                                "type" : "text",
                                "fields" : {
                                  "keyword" : {
                                    "type" : "keyword",
                                    "ignore_above" : 256
                                  }
                                }
                              },
                              "timestamp" : {
                                "type" : "text",
                                "fields" : {
                                  "keyword" : {
                                    "type" : "keyword",
                                    "ignore_above" : 256
                                  }
                                }
                              },
                              "uuid" : {
                                "type" : "text",
                                "fields" : {
                                  "keyword" : {
                                    "type" : "keyword",
                                    "ignore_above" : 256
                                  }
                                }
                              }
                            }
                          }
                        }
                      },
                      "Tag" : {
                        "properties" : {
                          "colour" : {
                            "type" : "text",
                            "fields" : {
                              "keyword" : {
                                "type" : "keyword",
                                "ignore_above" : 256
                              }
                            }
                          },
                          "exportable" : {
                            "type" : "boolean"
                          },
                          "hide_tag" : {
                            "type" : "boolean"
                          },
                          "id" : {
                            "type" : "text",
                            "fields" : {
                              "keyword" : {
                                "type" : "keyword",
                                "ignore_above" : 256
                              }
                            }
                          },
                          "name" : {
                            "type" : "text",
                            "fields" : {
                              "keyword" : {
                                "type" : "keyword",
                                "ignore_above" : 256
                              }
                            }
                          }
                        }
                      },
                      "analysis" : {
                        "type" : "text",
                        "fields" : {
                          "keyword" : {
                            "type" : "keyword",
                            "ignore_above" : 256
                          }
                        }
                      },
                      "attribute_count" : {
                        "type" : "text",
                        "fields" : {
                          "keyword" : {
                            "type" : "keyword",
                            "ignore_above" : 256
                          }
                        }
                      },
                      "date" : {
                        "type" : "date"
                      },
                      "disable_correlation" : {
                        "type" : "boolean"
                      },
                      "distribution" : {
                        "type" : "text",
                        "fields" : {
                          "keyword" : {
                            "type" : "keyword",
                            "ignore_above" : 256
                          }
                        }
                      },
                      "id" : {
                        "type" : "text",
                        "fields" : {
                          "keyword" : {
                            "type" : "keyword",
                            "ignore_above" : 256
                          }
                        }
                      },
                      "info" : {
                        "type" : "text",
                        "fields" : {
                          "keyword" : {
                            "type" : "keyword",
                            "ignore_above" : 256
                          }
                        }
                      },
                      "locked" : {
                        "type" : "boolean"
                      },
                      "org_id" : {
                        "type" : "text",
                        "fields" : {
                          "keyword" : {
                            "type" : "keyword",
                            "ignore_above" : 256
                          }
                        }
                      },
                      "orgc_id" : {
                        "type" : "text",
                        "fields" : {
                          "keyword" : {
                            "type" : "keyword",
                            "ignore_above" : 256
                          }
                        }
                      },
                      "proposal_email_lock" : {
                        "type" : "boolean"
                      },
                      "publish_timestamp" : {
                        "type" : "text",
                        "fields" : {
                          "keyword" : {
                            "type" : "keyword",
                            "ignore_above" : 256
                          }
                        }
                      },
                      "published" : {
                        "type" : "boolean"
                      },
                      "sharing_group_id" : {
                        "type" : "text",
                        "fields" : {
                          "keyword" : {
                            "type" : "keyword",
                            "ignore_above" : 256
                          }
                        }
                      },
                      "threat_level_id" : {
                        "type" : "text",
                        "fields" : {
                          "keyword" : {
                            "type" : "keyword",
                            "ignore_above" : 256
                          }
                        }
                      },
                      "timestamp" : {
                        "type" : "text",
                        "fields" : {
                          "keyword" : {
                            "type" : "keyword",
                            "ignore_above" : 256
                          }
                        }
                      },
                      "uuid" : {
                        "type" : "text",
                        "fields" : {
                          "keyword" : {
                            "type" : "keyword",
                            "ignore_above" : 256
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          },
          "dataParams" : {
            "properties" : {
              "applicationId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "cspId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "dateTime" : {
                "type" : "date"
              },
              "originApplicationId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "originCspId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "originRecordId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "recordId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "url" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              }
            }
          },
          "dataType" : {
            "type" : "text",
            "fields" : {
              "keyword" : {
                "type" : "keyword",
                "ignore_above" : 256
              }
            }
          },
          "sharingParams" : {
            "properties" : {
              "isExternal" : {
                "type" : "boolean"
              },
              "trustCircleId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "teamId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "toShare" : {
                "type" : "boolean"
              }
            }
          }
        }
}
'

  echo ""
  echo ""
  echo " threat CONFIGURATION"
  echo ""
  echo ""
  echo ""


curl -XPUT  'localhost:9200/cspdata/_mapping/threat?pretty' -H 'Content-Type: application/json' -d'
{
        "properties" : {
          "dataObject" : {
            "properties" : {
              "response" : {
                "properties" : {
                  "Event" : {
                    "properties" : {
                      "Attribute" : {
                        "properties" : {
                          "category" : {
                            "type" : "text",
                            "fields" : {
                              "keyword" : {
                                "type" : "keyword",
                                "ignore_above" : 256
                              }
                            }
                          },
                          "comment" : {
                            "type" : "text",
                            "fields" : {
                              "keyword" : {
                                "type" : "keyword",
                                "ignore_above" : 256
                              }
                            }
                          },
                          "deleted" : {
                            "type" : "boolean"
                          },
                          "disable_correlation" : {
                            "type" : "boolean"
                          },
                          "distribution" : {
                            "type" : "text",
                            "fields" : {
                              "keyword" : {
                                "type" : "keyword",
                                "ignore_above" : 256
                              }
                            }
                          },
                          "event_id" : {
                            "type" : "text",
                            "fields" : {
                              "keyword" : {
                                "type" : "keyword",
                                "ignore_above" : 256
                              }
                            }
                          },
                          "id" : {
                            "type" : "text",
                            "fields" : {
                              "keyword" : {
                                "type" : "keyword",
                                "ignore_above" : 256
                              }
                            }
                          },
                          "sharing_group_id" : {
                            "type" : "text",
                            "fields" : {
                              "keyword" : {
                                "type" : "keyword",
                                "ignore_above" : 256
                              }
                            }
                          },
                          "timestamp" : {
                            "type" : "text",
                            "fields" : {
                              "keyword" : {
                                "type" : "keyword",
                                "ignore_above" : 256
                              }
                            }
                          },
                          "to_ids" : {
                            "type" : "boolean"
                          },
                          "type" : {
                            "type" : "text",
                            "fields" : {
                              "keyword" : {
                                "type" : "keyword",
                                "ignore_above" : 256
                              }
                            }
                          },
                          "uuid" : {
                            "type" : "text",
                            "fields" : {
                              "keyword" : {
                                "type" : "keyword",
                                "ignore_above" : 256
                              }
                            }
                          },
                          "value" : {
                            "type" : "text",
                            "fields" : {
                              "keyword" : {
                                "type" : "keyword",
                                "ignore_above" : 256
                              }
                            }
                          }
                        }
                      },
                      "Org" : {
                        "properties" : {
                          "id" : {
                            "type" : "text",
                            "fields" : {
                              "keyword" : {
                                "type" : "keyword",
                                "ignore_above" : 256
                              }
                            }
                          },
                          "name" : {
                            "type" : "text",
                            "fields" : {
                              "keyword" : {
                                "type" : "keyword",
                                "ignore_above" : 256
                              }
                            }
                          },
                          "uuid" : {
                            "type" : "text",
                            "fields" : {
                              "keyword" : {
                                "type" : "keyword",
                                "ignore_above" : 256
                              }
                            }
                          }
                        }
                      },
                      "Orgc" : {
                        "properties" : {
                          "id" : {
                            "type" : "text",
                            "fields" : {
                              "keyword" : {
                                "type" : "keyword",
                                "ignore_above" : 256
                              }
                            }
                          },
                          "name" : {
                            "type" : "text",
                            "fields" : {
                              "keyword" : {
                                "type" : "keyword",
                                "ignore_above" : 256
                              }
                            }
                          },
                          "uuid" : {
                            "type" : "text",
                            "fields" : {
                              "keyword" : {
                                "type" : "keyword",
                                "ignore_above" : 256
                              }
                            }
                          }
                        }
                      },
                      "RelatedEvent" : {
                        "properties" : {
                          "Event" : {
                            "properties" : {
                              "Org" : {
                                "properties" : {
                                  "id" : {
                                    "type" : "text",
                                    "fields" : {
                                      "keyword" : {
                                        "type" : "keyword",
                                        "ignore_above" : 256
                                      }
                                    }
                                  },
                                  "name" : {
                                    "type" : "text",
                                    "fields" : {
                                      "keyword" : {
                                        "type" : "keyword",
                                        "ignore_above" : 256
                                      }
                                    }
                                  },
                                  "uuid" : {
                                    "type" : "text",
                                    "fields" : {
                                      "keyword" : {
                                        "type" : "keyword",
                                        "ignore_above" : 256
                                      }
                                    }
                                  }
                                }
                              },
                              "Orgc" : {
                                "properties" : {
                                  "id" : {
                                    "type" : "text",
                                    "fields" : {
                                      "keyword" : {
                                        "type" : "keyword",
                                        "ignore_above" : 256
                                      }
                                    }
                                  },
                                  "name" : {
                                    "type" : "text",
                                    "fields" : {
                                      "keyword" : {
                                        "type" : "keyword",
                                        "ignore_above" : 256
                                      }
                                    }
                                  },
                                  "uuid" : {
                                    "type" : "text",
                                    "fields" : {
                                      "keyword" : {
                                        "type" : "keyword",
                                        "ignore_above" : 256
                                      }
                                    }
                                  }
                                }
                              },
                              "analysis" : {
                                "type" : "text",
                                "fields" : {
                                  "keyword" : {
                                    "type" : "keyword",
                                    "ignore_above" : 256
                                  }
                                }
                              },
                              "date" : {
                                "type" : "date"
                              },
                              "distribution" : {
                                "type" : "text",
                                "fields" : {
                                  "keyword" : {
                                    "type" : "keyword",
                                    "ignore_above" : 256
                                  }
                                }
                              },
                              "id" : {
                                "type" : "text",
                                "fields" : {
                                  "keyword" : {
                                    "type" : "keyword",
                                    "ignore_above" : 256
                                  }
                                }
                              },
                              "info" : {
                                "type" : "text",
                                "fields" : {
                                  "keyword" : {
                                    "type" : "keyword",
                                    "ignore_above" : 256
                                  }
                                }
                              },
                              "org_id" : {
                                "type" : "text",
                                "fields" : {
                                  "keyword" : {
                                    "type" : "keyword",
                                    "ignore_above" : 256
                                  }
                                }
                              },
                              "orgc_id" : {
                                "type" : "text",
                                "fields" : {
                                  "keyword" : {
                                    "type" : "keyword",
                                    "ignore_above" : 256
                                  }
                                }
                              },
                              "published" : {
                                "type" : "boolean"
                              },
                              "threat_level_id" : {
                                "type" : "text",
                                "fields" : {
                                  "keyword" : {
                                    "type" : "keyword",
                                    "ignore_above" : 256
                                  }
                                }
                              },
                              "timestamp" : {
                                "type" : "text",
                                "fields" : {
                                  "keyword" : {
                                    "type" : "keyword",
                                    "ignore_above" : 256
                                  }
                                }
                              },
                              "uuid" : {
                                "type" : "text",
                                "fields" : {
                                  "keyword" : {
                                    "type" : "keyword",
                                    "ignore_above" : 256
                                  }
                                }
                              }
                            }
                          }
                        }
                      },
                      "Tag" : {
                        "properties" : {
                          "colour" : {
                            "type" : "text",
                            "fields" : {
                              "keyword" : {
                                "type" : "keyword",
                                "ignore_above" : 256
                              }
                            }
                          },
                          "exportable" : {
                            "type" : "boolean"
                          },
                          "hide_tag" : {
                            "type" : "boolean"
                          },
                          "id" : {
                            "type" : "text",
                            "fields" : {
                              "keyword" : {
                                "type" : "keyword",
                                "ignore_above" : 256
                              }
                            }
                          },
                          "name" : {
                            "type" : "text",
                            "fields" : {
                              "keyword" : {
                                "type" : "keyword",
                                "ignore_above" : 256
                              }
                            }
                          }
                        }
                      },
                      "analysis" : {
                        "type" : "text",
                        "fields" : {
                          "keyword" : {
                            "type" : "keyword",
                            "ignore_above" : 256
                          }
                        }
                      },
                      "attribute_count" : {
                        "type" : "text",
                        "fields" : {
                          "keyword" : {
                            "type" : "keyword",
                            "ignore_above" : 256
                          }
                        }
                      },
                      "date" : {
                        "type" : "date"
                      },
                      "disable_correlation" : {
                        "type" : "boolean"
                      },
                      "distribution" : {
                        "type" : "text",
                        "fields" : {
                          "keyword" : {
                            "type" : "keyword",
                            "ignore_above" : 256
                          }
                        }
                      },
                      "id" : {
                        "type" : "text",
                        "fields" : {
                          "keyword" : {
                            "type" : "keyword",
                            "ignore_above" : 256
                          }
                        }
                      },
                      "info" : {
                        "type" : "text",
                        "fields" : {
                          "keyword" : {
                            "type" : "keyword",
                            "ignore_above" : 256
                          }
                        }
                      },
                      "locked" : {
                        "type" : "boolean"
                      },
                      "org_id" : {
                        "type" : "text",
                        "fields" : {
                          "keyword" : {
                            "type" : "keyword",
                            "ignore_above" : 256
                          }
                        }
                      },
                      "orgc_id" : {
                        "type" : "text",
                        "fields" : {
                          "keyword" : {
                            "type" : "keyword",
                            "ignore_above" : 256
                          }
                        }
                      },
                      "proposal_email_lock" : {
                        "type" : "boolean"
                      },
                      "publish_timestamp" : {
                        "type" : "text",
                        "fields" : {
                          "keyword" : {
                            "type" : "keyword",
                            "ignore_above" : 256
                          }
                        }
                      },
                      "published" : {
                        "type" : "boolean"
                      },
                      "sharing_group_id" : {
                        "type" : "text",
                        "fields" : {
                          "keyword" : {
                            "type" : "keyword",
                            "ignore_above" : 256
                          }
                        }
                      },
                      "threat_level_id" : {
                        "type" : "text",
                        "fields" : {
                          "keyword" : {
                            "type" : "keyword",
                            "ignore_above" : 256
                          }
                        }
                      },
                      "timestamp" : {
                        "type" : "text",
                        "fields" : {
                          "keyword" : {
                            "type" : "keyword",
                            "ignore_above" : 256
                          }
                        }
                      },
                      "uuid" : {
                        "type" : "text",
                        "fields" : {
                          "keyword" : {
                            "type" : "keyword",
                            "ignore_above" : 256
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          },
          "dataParams" : {
            "properties" : {
              "applicationId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "cspId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "dateTime" : {
                "type" : "date"
              },
              "originApplicationId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "originCspId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "originRecordId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "recordId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "url" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              }
            }
          },
          "dataType" : {
            "type" : "text",
            "fields" : {
              "keyword" : {
                "type" : "keyword",
                "ignore_above" : 256
              }
            }
          },
          "sharingParams" : {
            "properties" : {
              "isExternal" : {
                "type" : "boolean"
              },
              "trustCircleId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "teamId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "toShare" : {
                "type" : "boolean"
              }
            }
          }
        }
}
'

  echo ""
  echo ""
  echo " incident CONFIGURATION"
  echo ""
  echo ""
  echo ""

curl -XPUT  'localhost:9200/cspdata/_mapping/incident?pretty' -H 'Content-Type: application/json' -d'
{
        "properties" : {
          "dataObject" : {
            "properties" : {
              "created" : {
                "type" : "long"
              },
              "creator" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "customFields" : {
                "properties" : {
                  "Additional data" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "Customer" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "How Reported" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "IP" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "Linked events" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "Linked threats" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "Linked vulnerabilities" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "RT_UUID" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "Reporter Type" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "Sharing policy" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  }
                }
              },
              "id" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "lastUpdated" : {
                "type" : "long"
              },
              "owner" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "queue" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "sharing" : {
                "type" : "boolean"
              },
              "status" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "subject" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "timeEstimated" : {
                "type" : "long"
              },
              "timeLeft" : {
                "type" : "long"
              },
              "timeWorked" : {
                "type" : "long"
              },
              "uuid" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              }
            }
          },
          "dataParams" : {
            "properties" : {
              "applicationId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "cspId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "dateTime" : {
                "type" : "date"
              },
              "originApplicationId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "originCspId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "originRecordId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "recordId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "url" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              }
            }
          }
        }
}
'

  echo ""
  echo ""
  echo " file CONFIGURATION"
  echo ""
  echo ""
  echo ""

curl -XPUT  'localhost:9200/cspdata/_mapping/file?pretty' -H 'Content-Type: application/json' -d'
{
        "properties" : {
          "dataObject" : {
            "properties" : {
              "fileinfo" : {
                "properties" : {
                  "Checksum" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "Etag" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "Id" : {
                    "type" : "long"
                  },
                  "InternalPath" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "MimePart" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "MimeType" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "MountPoint" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "Mtime" : {
                    "type" : "long"
                  },
                  "Name" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "Owner" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "Path" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "Permissions" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "Size" : {
                    "type" : "long"
                  },
                  "Storage" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "Type" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "isCreatable" : {
                    "type" : "boolean"
                  },
                  "isDeletable" : {
                    "type" : "boolean"
                  },
                  "isEncrypted" : {
                    "type" : "boolean"
                  },
                  "isMounted" : {
                    "type" : "boolean"
                  },
                  "isReadable" : {
                    "type" : "boolean"
                  },
                  "isShareable" : {
                    "type" : "boolean"
                  },
                  "isShared" : {
                    "type" : "boolean"
                  },
                  "isUpdateable" : {
                    "type" : "boolean"
                  }
                }
              }
            }
          },
          "dataParams" : {
            "properties" : {
              "applicationId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "cspId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "dateTime" : {
                "type" : "date"
              },
              "originApplicationId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "originCspId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "originRecordId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "recordId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "url" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              }
            }
          },
          "dataType" : {
            "type" : "text",
            "fields" : {
              "keyword" : {
                "type" : "keyword",
                "ignore_above" : 256
              }
            }
          },
          "sharingParams" : {
            "properties" : {
              "isExternal" : {
                "type" : "boolean"
              },
              "trustCircleId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "teamId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "toShare" : {
                "type" : "boolean"
              }
            }
          }
        }
}
'


  echo ""
  echo ""
  echo " contact CONFIGURATION"
  echo ""
  echo ""
  echo ""

curl -XPUT  'localhost:9200/cspdata/_mapping/contact?pretty' -H 'Content-Type: application/json' -d'
{
        "properties" : {
          "dataObject" : {
            "properties" : {
              "contact" : {
                "properties" : {
                  "active" : {
                    "type" : "boolean"
                  },
                  "address_id" : {
                    "type" : "long"
                  },
                  "birthdate" : {
                    "type" : "date"
                  },
                  "comment" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "email" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "email_priv" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "fax" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "firstname" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "import_source" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "import_time" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "lastname" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "maintained_by" : {
                    "type" : "long"
                  },
                  "mobile" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "mobile_priv" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "organisation_id" : {
                    "type" : "long"
                  },
                  "pgp_key_id" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "picture" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "smime_certificate" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "tel" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "tel_priv" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "title" : {
                    "type" : "text",
                    "fields" : {
                      "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                      }
                    }
                  },
                  "vouched_by" : {
                    "type" : "long"
                  }
                }
              }
            }
          },
          "dataParams" : {
            "properties" : {
              "applicationId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "cspId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "dateTime" : {
                "type" : "date"
              },
              "originApplicationId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "originCspId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "originRecordId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "recordId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "url" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              }
            }
          },
          "dataType" : {
            "type" : "text",
            "fields" : {
              "keyword" : {
                "type" : "keyword",
                "ignore_above" : 256
              }
            }
          },
          "sharingParams" : {
            "properties" : {
              "isExternal" : {
                "type" : "boolean"
              },
              "trustCircleId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "teamId" : {
                "type" : "text",
                "fields" : {
                  "keyword" : {
                    "type" : "keyword",
                    "ignore_above" : 256
                  }
                }
              },
              "toShare" : {
                "type" : "boolean"
              }
            }
          }
        }
}
'
