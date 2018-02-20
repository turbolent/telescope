import axios from 'axios'
import { decodeResults, Parse, Result } from './types'

const PARSE_API_PATH = '/api/parse'
const QUERY_API_URL = 'https://query.wikidata.org/bigdata/namespace/wdq/sparql'

export type Cancel = (message?: string) => void

export const parse = (question: string):
    [Promise<Parse>, Cancel] => {

    const source = axios.CancelToken.source()

    const params = {
        sentence: question,
        result: 'queries',
        label: true,
        wikipediaTitle: true,
        wikipediaTitleOptional: true
    }

    const promise = axios.get(PARSE_API_PATH, {
        params,
        cancelToken: source.token
    })
        .then(response => Parse.decode(response.data))
        .catch(e => {
            if (axios.isCancel(e)) {
                return
            }

            throw e
        }) as Promise<Parse>

    return [
        promise,
        source.cancel.bind(source)
    ]
}

export const search = (query: string):
    [Promise<Result[]>, Cancel] => {

    const source = axios.CancelToken.source()

    const promise = axios.get(QUERY_API_URL, {
        params: {query},
        cancelToken: source.token,
        headers: {'accept': 'application/sparql-results+json'}
    })
        .then(response => decodeResults(response.data))
        .catch(e => {
            if (axios.isCancel(e)) {
                return
            }

            throw e
        }) as Promise<Result[]>

    return [
        promise,
        source.cancel.bind(source)
    ]
}
