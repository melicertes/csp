package RT::Condition::CSP_ToEmitter;
use base 'RT::Condition';

use strict;
use warnings;


sub IsApplicable {
	my $self = shift;
	$RT::Logger->debug("CSP_ToEmitter: processing ...");	

	# do not fire if RT_UUID has been set
	if ($self->TransactionObj->Type eq 'CustomField') {
		my $cf = RT::CustomField->new( $self->CurrentUser );
		$cf->Load( $self->TransactionObj->Field );
	 	if ($cf->Name eq 'RT_UUID') {   
			$RT::Logger->debug("RT_UUID has been changed -> do not fire scrip!");
			return 0;
		}	
	}
	return 1;
}

RT::Base->_ImportOverlays();

1;
