package RT::Action::CSPOnCreateAction;

use strict;
use warnings;

use base 'RT::Action';
use REST::Client;

sub Prepare {
	return 1;
}

sub Commit {
	$RT::Logger->debug("CSPOnCreateAction: processing...\n");
	my $self = shift;

	my $CFName3 = 'Last update done by';
	
	my $RT_EMITTER_URL = RT->Config->Get('RT_EMITTER_URL');
	my $RT_CLIENT_KEY = RT->Config->Get('RT_CLIENT_KEY');
	my $RT_CLIENT_CRT = RT->Config->Get('RT_CLIENT_CRT');
	my $RT_SRV_CA_CRT = RT->Config->Get('RT_SRV_CA_CRT');

	if ( not defined $RT_EMITTER_URL or $RT_EMITTER_URL eq '')
	{
		$RT::Logger->error("CSPOnCreateAction: RT_EMITTER_URL missing!\n");
		return 0;
	}

	if ( not defined $RT_CLIENT_KEY or $RT_CLIENT_KEY eq '')
        {
                $RT::Logger->error("CSPOnCreateAction: RT_CLIENT_KEY missing!\n");
                return 0;
        }

	if ( not defined $RT_CLIENT_CRT or $RT_CLIENT_CRT eq '')
        {
                $RT::Logger->error("CSPOnCreateAction: RT_CLIENT_CRT missing!\n");
                return 0;
        }

	if ( not defined $RT_SRV_CA_CRT or $RT_SRV_CA_CRT eq '')
        {
                $RT::Logger->error("CSPOnCreateAction: RT_SRV_CA_CRT missing!\n");
                return 0;
        }

	my $TransType = $self->TransactionObj->Type;
	$RT::Logger->debug("CSPOnCreateAction: transaction type = $TransType \n");
	if( defined $TransType and (
 	      $TransType eq 'Create' or 
	      $TransType eq 'Status' or 
              $TransType eq 'Comment' or
	      $TransType eq 'CustomField' or
	      $TransType eq 'Subject' or 
	      $TransType eq 'AddLink' or		# ???
	      $TransType eq 'DeleteLink' or		# ???
              $TransType eq 'Set') )
	{
		$RT::Logger->debug("CSPOnCreateAction: UPDATE OR CREATE!");
		
		if ( $TransType ne 'Create') # -> something has been updated!
		{
			$RT::Logger->debug("CSPOnCreateAction: ONLY UPDATE!");

			my $RT_CSP_NAME = RT->Config->Get( 'RT_CSP_NAME' );
			
			if ( defined $self->TicketObj->FirstCustomFieldValue( $CFName3 ) and
		   	     0 == index( $self->TicketObj->FirstCustomFieldValue( $CFName3 ), 'adapter:' ) )
		 	{
				$RT::Logger->debug("CSPOnCreateAction: this update is comming from adapter -> keep the value of last update\n");
				my $totalLen = length $self->TicketObj->FirstCustomFieldValue( $CFName3 );
				my $strLen = $totalLen - 8;
				my $strValue = substr( $self->TicketObj->FirstCustomFieldValue( $CFName3 ), 8, $strLen );
				$RT::Logger->debug("CSPOnCreateAction: keep the last update done to: " . $strValue . "!\n");
				$self->TicketObj->AddCustomFieldValue(
					Field		  => $CFName3,
					Value		  => $strValue,
					RecordTransaction => 0 );
			} else {
				$RT::Logger->debug("CSPOnCreateAction: working with following Last Updated CSP: $RT_CSP_NAME \n");
				$self->TicketObj->AddCustomFieldValue(
					Field		  => $CFName3,
					Value		  => $RT_CSP_NAME,
					RecordTransaction => 0 );
			}
		}
	
		$RT::Logger->debug("CSPOnCreateAction: commiting the id of new created ticket\n");

		$RT::Logger->debug("CSPOnCreateAction: working with following connection data:\n");
		$RT::Logger->debug(" RT_EMITTER_URL: " . $RT_EMITTER_URL . "\n");
		$RT::Logger->debug(" RT_CLIENT_KEY : " . $RT_CLIENT_KEY . "\n");
		$RT::Logger->debug(" RT_CLIENT_CRT : " . $RT_CLIENT_CRT . "\n");
		$RT::Logger->debug(" RT_SRV_CA_CRT : " . $RT_SRV_CA_CRT . "\n");

		my $client = REST::Client->new( 
			host 		=> $RT_EMITTER_URL,
			key		=> $RT_CLIENT_KEY,
			cert		=> $RT_CLIENT_CRT,
			ca		=> $RT_SRV_CA_CRT,);
		my $ticketId = $self->TicketObj->id;

		$RT::Logger->debug("CSPOnCreateAction: requesting data of ticket with id: " . $ticketId . "\n");

		$client->GET( "/rt/emitter/$ticketId" );
		
		$RT::Logger->debug("CSPOnCreateAction: Got response " . $client->responseCode() . "\n");
	
		$RT::Logger->debug("CSPOnCreateAction: Commit done -> exiting the subfunction \n");
	}
	return 1;
}

1;
