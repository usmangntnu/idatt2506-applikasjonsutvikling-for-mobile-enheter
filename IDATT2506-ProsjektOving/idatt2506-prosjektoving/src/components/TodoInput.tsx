import React, {useRef, useState} from 'react';
import { IonInput, IonButton } from '@ionic/react';

//Function received from App.tsx to add elements to chosen list
interface Props {
    addTodo: (text: string) => void;
}

// KRAV 4: Component itself, has input field to add some new element
const TodoInput: React.FC<Props> = ({ addTodo }) => {
    const [text, setText] = useState('');
    const inputRef = useRef<HTMLIonInputElement>(null);
    //KRAV 5: Handles enter key press to add new element
    const handleEnter = (e: React.KeyboardEvent) => {
        if (e.key === 'Enter' && text.trim() !== '') {
            addTodo(text.trim());
            setText('');
            //KRAV 5: Here to keep focus on input after one element is added
            setTimeout(() => inputRef.current?.setFocus(), 100);
        }
    };
    //KRAV 4 og 5: Field to add a new element and triggers keyboard on focus
    return (
        <IonInput
            ref={inputRef}
            value={text}
            placeholder="Legg til nytt element"
            onIonChange={e => setText(e.detail.value!)}
            onKeyPress={handleEnter}
        />
    );
};

export default TodoInput;
