import React from 'react';
import styled from 'styled-components';

import { colors } from '@atlaskit/theme';

export const ClearButton = (props) => {
    const Container = styled.button`
        border: none;
        display: flex;
        background-color: inherit;
        cursor: pointer;
        align-items: center;
        text-align: center;
        padding: 0px;
        border-radius: 50%;
    `;

    const Icon = styled.i`
        color: ${colors.N80};
        font-size: 16px;
        padding: 0 4px;

        &:hover {
            color: ${colors.N800};
        }
    `;

    return (
        props.isVisible ?
            <Container onClick={props.onClick}>
                <Icon className='material-icons'>cancel</Icon>
            </Container> :
            <></>
    );
}