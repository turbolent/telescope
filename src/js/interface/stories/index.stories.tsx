import * as React from 'react'

import { storiesOf } from '@storybook/react'
import Result from '../src/components/Result/Result'
import 'typeface-fira-sans'
import Progress from '../src/components/Progress/Progress'
import { withKnobs, boolean } from '@storybook/addon-knobs'

const resultWrapperStyle = {width: 300, height: 330}

storiesOf('Result', module)
    .add('Leonardo da Vinci', () => (
        <div style={resultWrapperStyle}>
            <Result
                imageURL="https://upload.wikimedia.org/wikipedia/commons/thumb/f/f7/Francesco_Melzi_-_Portrait_of_Leonardo_-_WGA14795.jpg/223px-Francesco_Melzi_-_Portrait_of_Leonardo_-_WGA14795.jpg"
                label="Leonardo da Vinci"
                description="Italian Renaissance polymath"
                extractHTML='<p><b>Leonardo di ser Piero da Vinci</b> (<small>Italian: </small><span title=\"Representation in the International Phonetic Alphabet (IPA)\">[leoˈnardo di ˌsɛr ˈpjɛːro da (v)ˈvintʃi]</span><small class=\"nowrap\"> (<span><span><span> </span>listen</span></span>)</small>; 15 April 1452 – 2 May 1519), more commonly <b>Leonardo da Vinci</b> or simply <b>Leonardo</b>, was an Italian Renaissance polymath whose areas of interest included invention, painting, sculpting, architecture, science, music, mathematics, engineering, literature, anatomy, geology, astronomy, botany, writing, history, and cartography. He has been variously called the father of palaeontology, ichnology, and architecture, and is widely considered one of the greatest painters of all time. Sometimes credited with the inventions of the parachute, helicopter and tank, he epitomised the Renaissance humanist ideal.</p>\n<p>Many historians and scholars regard Leonardo as the prime exemplar of the \"Universal Genius\" or \"Renaissance Man\", an individual of \"unquenchable curiosity\" and \"feverishly inventive imagination\", and he is widely considered one of the most diversely talented individuals ever to have lived. According to art historian Helen Gardner, the scope and depth of his interests were without precedent in recorded history, and \"his mind and personality seem to us superhuman, while the man himself mysterious and remote\". Marco Rosci notes that while there is much speculation regarding his life and personality, his view of the world was logical rather than mysterious, and that the empirical methods he employed were unorthodox for his time.</p>\n<p>Born out of wedlock to a notary, Piero da Vinci, and a peasant woman, Caterina, in Vinci in the region of Florence, Leonardo was educated in the studio of the renowned Florentine painter Andrea del Verrocchio.'
            />
        </div>
         )
    )
    .add('Nineteen Eighty-Four', () => (
        <div style={resultWrapperStyle}>
            <Result
                imageURL="https://upload.wikimedia.org/wikipedia/en/thumb/c/c3/1984first.jpg/215px-1984first.jpg"
                label="Nineteen Eighty-Four"
                description="dystopian novel written by George Orwell"
                extractHTML={"<p><i><b>Nineteen Eighty-Four</b></i>, often published as <i><b>1984</b></i>, is a dystopian novel published in 1949 by English author George Orwell. The novel is set in Airstrip One, formerly Great Britain, a province of the superstate Oceania, whose residents are victims of perpetual war, omnipresent government surveillance and public manipulation. Oceania's political ideology, euphemistically named English Socialism (shortened to \\\"Ingsoc\\\" in Newspeak, the government's invented language) is enforced by the privileged, elite Inner Party. Via the \\\"Thought Police\\\", the Inner Party persecutes individualism and independent thinking, which are regarded as \\\"thoughtcrimes\\\".</p>\\n<p>The tyranny is ostensibly overseen by a mysterious leader known as Big Brother, who enjoys an intense cult of personality. The Party \\\"seeks power entirely for its own sake."}
            />
        </div>
         )
    )

storiesOf('Progress', module)
    .addDecorator(withKnobs)
    .add('Progress', () => (
        <Progress
            active={boolean('Active', false)}
            hidden={boolean('Hidden', false)}
        />
    ))
