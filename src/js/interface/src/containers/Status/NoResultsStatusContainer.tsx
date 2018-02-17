import { State } from '../../state'
import { connect } from 'react-redux'
import Status, { Props } from '../../components/Status/Status'
import { Result } from '../../types'

function getMessage(question: string,
                    requesting: boolean,
                    error?: string,
                    results?: Result[]): String | undefined {

    if (error === undefined
        && question !== ''
        && !requesting
        && !(results && results.length)) {

        return 'No results'
    }

    return
}

const mapStateToProps = ({question, requesting, error, results}: State): Props => ({
    message: getMessage(question, requesting, error, results)
})

export default connect(mapStateToProps)(Status)
