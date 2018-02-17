import { State } from '../../state'
import { connect } from 'react-redux'
import Examples, { Props } from '../../components/Examples/Examples'

const mapStateToProps = ({question}: State): Props => ({
    hidden: question !== ''
})

export default connect(mapStateToProps)(Examples)
