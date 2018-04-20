package RT::CustomFieldValues::CustomTCSource;

use strict;
use warnings;

use Cpanel::JSON::XS qw(decode_json);
use Data::Dumper;

use REST::Client;

# define class inheritance
use base qw(RT::CustomFieldValues::External);

# admin friendly description, the default valuse is the name of the class
sub SourceDescription {
  return 'Trust circles and Teams';
}
  
# actual values provider method
sub ExternalValues {
  # return reference to array ([])

  my @arr0;
  my $index = 0;  
  my $json;
  my $json_object;
  my $file1 = "/opt/rt4/local/lib/RT/CustomFieldValues/tc-list.json";
  my $file2 = "/opt/rt4/local/lib/RT/CustomFieldValues/teams-list.json";
  my $RT_TC_MOCK = RT->Config->Get('RT_TC_MOCK');

  RT::Logger->debug("CustomTCSource: push default values into array");
  push @arr0, { name => 'no sharing', 
                description => 'Data won\'t be shared', 
                sortorder => $index++, 
                category => 'Default'};
  push @arr0, { name => 'default sharing', 
                description => 'Use default sharing policy', 
                sortorder => $index++, 
                category => 'Default'};

  if ( defined $RT_TC_MOCK and $RT_TC_MOCK eq 'on')
  {
    RT::Logger->debug("Using the mock -> reading data from files");

    RT::Logger->debug("CustomTCSource: push trust circles into array");
    {
      RT::Logger->debug("CustomTCSource: reading and parsing trust circles");
      local $/; #Enable 'slurp' mode
      open my $fh, "<", $file1;
      $json = <$fh>;
      close $fh;
    }

    $json_object = decode_json($json);

    for my $item(@{$json_object}) {
      push @arr0, { name => $item->{short_name}, 
                  description => $item->{description}, 
                  sortorder => $index++, 
                  category => 'Trust circles'};
    }

    RT::Logger->debug("CustomTCSource: push teams into array");
    {
      RT::Logger->debug("CustomTCSource: reading and parsing trust circles");
      local $/; #Enable 'slurp' mode
      open my $fh, "<", $file2;
      $json = <$fh>;
      close $fh;
    }

    $json_object = decode_json($json);

    for my $item(@{$json_object}) {
      push @arr0, { name => $item->{short_name}, 
                  description => $item->{description}, 
                  sortorder => $index++, 
                  category => 'Teams'};
    }
  }
  else # no mock
  {
    $RT::Logger->debug("CustomTCSource: obtain TCs and Teams fomr TC-app directly");
    my $RT_TC_URL     = RT->Config->Get('RT_TC_URL');
    my $RT_CLIENT_KEY = RT->Config->Get('RT_CLIENT_KEY');
    my $RT_CLIENT_CRT = RT->Config->Get('RT_CLIENT_CRT');
    my $RT_SRV_CA_CRT = RT->Config->Get('RT_SRV_CA_CRT');

    $RT::Logger->debug("CustomTCSource: RT_TC_URL    : " . $RT_TC_URL . "\n");
    $RT::Logger->debug("CustomTCSource: RT_CLIENT_KEY: " . $RT_CLIENT_KEY . "\n");
    $RT::Logger->debug("CustomTCSource: RT_CLIENT_CRT: " . $RT_CLIENT_CRT . "\n");
    $RT::Logger->debug("CustomTCSource: RT_SRV_CA_CRT: " . $RT_SRV_CA_CRT . "\n");

    if ( not defined $RT_TC_URL or $RT_TC_URL eq '' )
    {
      $RT::Logger->error("CustomTCSource: RT_TC_URL missing!\n");
      return 0;
    }
    # to be continued wiht error handling
    if ( not defined $RT_CLIENT_KEY or $RT_CLIENT_KEY eq '' )
    {
      $RT::Logger->error("CustomTCSource: missing RT_CLIENT_KEY!\n");
      return 0;
    }
    if ( not defined $RT_CLIENT_CRT or $RT_CLIENT_CRT eq '' )
    {
      $RT::Logger->error("CustomTCSource: missing RT_CLIENT_CRT!\n");
      return 0;
    }
    if ( not defined $RT_SRV_CA_CRT or $RT_SRV_CA_CRT eq '' )
    {
      $RT::Logger->error("CustomTCSource: missing RT_SRV_CA_CRT!\n");
      return 0;
    }

    my $client = REST::Client->new({
	    host	=> $RT_TC_URL,
	    key		=> $RT_CLIENT_KEY,
	    cert	=> $RT_CLIENT_CRT,
	    ca		=> $RT_SRV_CA_CRT,});

    my $tcPath 		= '/api/v1/circles';
    my $ltcPath 	= '/api/v1/ltc';
    my $teamsPath 	= '/api/v1/teams';
    
    $RT::Logger->debug("CustomTCSource: obtain the trust circles...");
    {  
      $RT::Logger->debug("CustomTCSource: calling the TC Rest service ...");
      $client->GET( $tcPath );

      if ( $client->responseCode() != 200 ) 
      {
         $RT::Logger->error("CustomTCSource: Getting Trust Circles failed: response code: " . $client->responseCode() ."\n");
	 return 0;
      }
      my $content = $client->responseContent();
      $json_object = decode_json( $content );

      for my $item(@{$json_object}) 
      {
      	push @arr0, { name        => $item->{short_name}, 
                      description => $item->{description}, 
                      sortorder   => $index++, 
                      category    => 'Trust Circles'};
      }
    }

    $RT::Logger->debug("CustomTCSource: obtain the LTCs ...");
    {  
      $RT::Logger->debug("CustomTCSource: calling the LTC Rest service ...");
      $client->GET( $ltcPath );

      if ( $client->responseCode() != 200 ) 
      {
         $RT::Logger->error("CustomTCSource: Getting Trust Circles failed: response code: " . $client->responseCode() ."\n");
	 return 0;
      }
      my $content = $client->responseContent();
      $json_object = decode_json( $content );

      for my $item(@{$json_object}) 
      {
      	push @arr0, { name        => $item->{short_name}, 
                      description => $item->{description}, 
                      sortorder   => $index++, 
                      category    => 'Local Trust Circles'};
      }
    }


    $RT::Logger->debug("CustomTCSource: obtain the teams ...");
    {
      $client->GET( $teamsPath );
      if ( $client->responseCode() != 200 ) 
      {
         $RT::Logger->error("CustomTCSource: Getting Teams failed: response code: " . $client->responseCode() ."\n");
	 return 0;
      }
      my $content = $client->responseContent();
      $json_object = decode_json( $content );

      for my $item(@{$json_object}) 
      {
      	push @arr0, { name        => $item->{short_name}, 
                      description => $item->{description}, 
                      sortorder   => $index++, 
                      category    => 'Teams'};
      }
    }
  }
  
  RT::Logger->debug("CustomTCSource: returning the built array");
  return \@arr0;
}

  
1; # don't forget to return some true value
