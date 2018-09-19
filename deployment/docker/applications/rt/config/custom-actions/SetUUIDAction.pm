package RT::Action::SetUUIDAction;

use strict;
use warnings;

use base 'RT::Action';
use Data::UUID;

sub Prepare {
	return 1;
}

sub Commit {
	$RT::Logger->debug("SetUUIDAction: committing the creation of uuid !!!\n");

	my $self = shift;
	my $CFName = 'RT_UUID';
	my $CFName2 = 'Originator CSP';
	my $CFName3 = 'Last update done by';
	my $CFName4 = 'Sharing policy';

	# got data from adapter, some of the fields should not be changed:
	# UUID, Originator CSP, Last update done by
	if ( defined $self->TicketObj->FirstCustomFieldValue( $CFName ) and
	     $self->TicketObj->FirstCustomFieldValue( $CFName ) ne '' and
	     0 == index( $self->TicketObj->FirstCustomFieldValue( $CFName ), 'adapter:' ) )
	{
		$RT::Logger->debug("SetUUIDAction: got UUID from adapter, just use it!");
 		my $totalLen = length $self->TicketObj->FirstCustomFieldValue( $CFName );
		my $uuidLen = $totalLen - 8;
		my $uuidVal = substr( $self->TicketObj->FirstCustomFieldValue( $CFName ), 8, $uuidLen);	
		$RT::Logger->debug("SetUUIDAction: working with uuid: {" . $uuidVal ."}\n");
		$self->TicketObj->AddCustomFieldValue( 
                        Field => $CFName, 
                        Value => $uuidVal, 
                        RecordTransaction => 0);
		$RT::Logger->debug("SetUUIDAction: got Orginator from adapter, just use it!");
		$RT::Logger->debug("SetUUIDAction: working with originator csp: [" . $self->TicketObj->FirstCustomFieldValue( $CFName2 ) . "]\n");
		$RT::Logger->debug("SetUUIDAction: working with last update csp: [" . $self->TicketObj->FirstCustomFieldValue( $CFName3 ) . "]\n");
		return 1;
	}

	if ($self->TransactionObj->Type eq "Create" and 
	    $self->TicketObj->QueueObj->Name eq "Incidents") 
	{
		$RT::Logger->debug("SetUUIDAction: resetting the RT_UUID -> set to empty string\n");
		$RT::Logger->debug("SetUUIDAction: got uuid: " . $self->TicketObj->FirstCustomFieldValue( $CFName ) . "\n");
		$self->TicketObj->AddCustomFieldValue( 
                        Field => $CFName, 
                        Value => '', 
                        RecordTransaction => 0);
		$RT::Logger->debug("SetUUIDAction: resetting the Originator CSP -> set to empty string\n");
                $RT::Logger->debug("SetUUIDAction: got Originator CSP: " . $self->TicketObj->FirstCustomFieldValue( $CFName2 ) . "\n");
		$self->TicketObj->AddCustomFieldValue( 
                        Field => $CFName2, 
                        Value => '', 
                        RecordTransaction => 0);
		$RT::Logger->debug("SetUUIDAction: resetting the Last updated CSP -> set to empty string\n");
                $RT::Logger->debug("SetUUIDAction: got Last updated CSP: " . $self->TicketObj->FirstCustomFieldValue( $CFName3 ) . "\n");
		$self->TicketObj->AddCustomFieldValue( 
                        Field => $CFName3, 
                        Value => '', 
                        RecordTransaction => 0);
		#$RT::Logger->debug("SetUUIDAction: resetting the Sharing policy -> set to no sharing\n");
		#$self->TicketObj->AddCustomFieldValue(
		#	Field => $CFName4,
		#	Value => '',
		#	RecordTransaction => 0);

	}

	# new incident has been created -> set the all initial data: 
	# UUID, Sharing policy Originator CSP, Last update done by
	if ( not defined $self->TicketObj->FirstCustomFieldValue( $CFName ) 
	     or $self->TicketObj->FirstCustomFieldValue( $CFName ) eq '' ) {
  		my $ug = Data::UUID->new;
	  	my $uuid = $ug->create_str();
		$RT::Logger->debug("SetUUIDAction: using following uuid: $uuid \n");
  		$self->TicketObj->AddCustomFieldValue( 
          		Field => $CFName, 
          		Value => $uuid, 
          		RecordTransaction => 0);
		my $RT_CSP_NAME = RT->Config->Get('RT_CSP_NAME');
		$RT::Logger->debug("SetUUIDAction: using following Originator CSP: $RT_CSP_NAME \n");
		$self->TicketObj->AddCustomFieldValue(
                        Field => $CFName2,
                        Value => $RT_CSP_NAME,
                        RecordTransaction => 0);
		$RT::Logger->debug("SetUUIDAction: using following Last updated CSP: $RT_CSP_NAME \n");
		$self->TicketObj->AddCustomFieldValue(
                        Field => $CFName3,
                        Value => $RT_CSP_NAME,
                        RecordTransaction => 0);
		if ( not defined $self->TicketObj->FirstCustomFieldValue( $CFName4 )
		     or $self->TicketObj->FirstCustomFieldValue( $CFName4 ) eq '')
                {
			$RT::Logger->debug("SetUUIDAction: using following default Sharing policy: no sharing \n");
			$self->TicketObj->AddCustomFieldValue(
				Field => $CFName4,
				Value => 'no sharing',
				RecordTransaction => 0);
		}
	}
	return 1;
}

RT::Base->_ImportOverlays();

1;
