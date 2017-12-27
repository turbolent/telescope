import axios from 'axios';
import { Token } from './types';

export interface QueriesResponse {
    tokens: Token[];
    error?: string;
}

type Cancel = (message?: string) => void;

export const requestQueries = (sentence: string):
    [Promise<QueriesResponse>, Cancel] => {

    const source = axios.CancelToken.source();

    const promise = axios.get('/api/parse', {
        params: {sentence},
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
