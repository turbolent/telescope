import * as React from 'react';

interface Props {
    unit?: {name: string};
    value: number;
}

export default ({value, unit}: Props) => {
    const suffix = unit
        ? `(${unit.name})`
        : '';
    return <div>{value} {suffix}</div>;
};
