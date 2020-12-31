import React from 'react';

import GenericAutosuggestion from './GenericAutosuggestion.js';
import AuthorizableProcessorService from './services/AuthorizableProcessorService.js';

export default function AuthorizableAutosuggestion(props) {
    return (
        <GenericAutosuggestion
            noOptionsMessage={(inputValue) => `No authorizables found for "${inputValue}"`}
            noOptionEmptyMessage='Start typing to find a authorizable'
            {...props}
            formatOptionLabel={AuthorizableProcessorService.formatOptionLabel}
            optionsPostProcessor={AuthorizableProcessorService.processMembersOptions}
            parameters={{ autosuggestionType: 'authorizable' }}
        />
    );
}