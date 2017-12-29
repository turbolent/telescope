import axios from 'axios';
import { Token } from './types';

export interface Parse {
    readonly tokens: Token[];
    readonly error?: string;
}

export type Cancel = (message?: string) => void;

export const parse = (question: string):
    [Promise<Parse>, Cancel] => {

    const source = axios.CancelToken.source();

    const promise = axios.get('/api/parse', {
        params: {sentence: question},
        cancelToken: source.token
    })
        .then(response => response.data)
        .catch(e => {
            if (axios.isCancel(e)) {
                return;
            }

            throw e;
        });

    return [
        promise,
        source.cancel.bind(source)
    ];
};
