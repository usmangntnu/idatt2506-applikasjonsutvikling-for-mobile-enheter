import React, {useRef, useState} from 'react';
import { IonInput, IonButton } from '@ionic/react';

//Function received from App.tsx to add elements to chosen list
interface Props {
    addTodo: (text: string) => void;
}

//Component itself
const TodoInput: React.FC<Props> = ({ addTodo }) => {
    const [text, setText] = useState('');
    const inputRef = useRef<HTMLIonInputElement>(null);
    //Handles enter key press to add new element
    const handleEnter = (e: React.KeyboardEvent) => {
        if (e.key === 'Enter' && text.trim() !== '') {
            addTodo(text.trim());
            setText('');
            //Here to keep focus on input after one element is added
            setTimeout(() => inputRef.current?.setFocus(), 100);
        }
    };
    //Field to add a new element
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
