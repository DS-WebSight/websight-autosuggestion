
import React from 'react';
import Spinner from '@atlaskit/spinner';

export const LoadingWrapper = (props) => {
    const loadingWrapperStyle = {
        position: 'relative',
        pointerEvents: 'none',
        opacity: 0.6,
        filter: 'grayscale(50%)'
    }

    const loadingWrapperSpinnerStyle = {
        position: 'fixed',
        top: 0,
        bottom: 0,
        left: 0,
        right: 0,
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        ...props.spinnerStyle
    }

    const spinner = (
        <span style={loadingWrapperSpinnerStyle}>
            <Spinner />
        </span>
    );

    return (
        <div style={props.isLoading ? loadingWrapperStyle : null}>
            {props.children}
            {props.isLoading && spinner}
        </div>
    );
}