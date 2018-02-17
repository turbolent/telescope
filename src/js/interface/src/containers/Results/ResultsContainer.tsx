import { State } from '../../state'
import { connect } from 'react-redux'
import Results, { Props } from '../../components/Results/Results'

const mapStateToProps = ({parse, results}: State): Props => ({
    query: (parse && parse.queries && parse.queries[0]) || '',
    results: results || []
})

export default connect(mapStateToProps)(Results)
