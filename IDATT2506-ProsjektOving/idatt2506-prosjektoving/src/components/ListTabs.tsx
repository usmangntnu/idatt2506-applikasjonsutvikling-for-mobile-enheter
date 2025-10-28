import React, { useState } from 'react';
import { IonSegment, IonSegmentButton, IonLabel, IonButton } from '@ionic/react';
import { ToDoList } from '../models/types';

//What data and function are gotten from App.tsx
interface Props {
    lists: ToDoList[];
    activeIndex: number;
    setActiveIndex: (index: number) => void;
    addList: (name: string) => void;
    deleteList: (index: number) => void;
}

//KRAV 3: Component itself that displays lists using tabs
const ListTabs: React.FC<Props> = ({ lists, activeIndex, setActiveIndex, addList, deleteList }) => {
    const [newListName, setNewListName] = useState('');

    const handleEnter = (e: React.KeyboardEvent) => {
        if (e.key === 'Enter' && newListName.trim() !== '') {
            addList(newListName.trim());
            setNewListName('');
        }
    };

    //Switching between lists, adding lists  and deleting lists
    return (
        <>
            {/* KRAV 3: IonSegment used for tab-based list overview */}
            <IonSegment value={activeIndex.toString()}>
                {lists.map((list, idx) => (
                    <IonSegmentButton
                        key={idx}
                        value={idx.toString()}
                        onClick={() => setActiveIndex(idx)}>
                            <IonLabel>{list.name}</IonLabel>
                    </IonSegmentButton>
                ))}
            </IonSegment>

            {/* KRAV 2: Delete button for removing lists. In own row under the Tabs */}
            <div style={{ display: 'flex', justifyContent: 'space-around', marginTop: '8px' }}>
                {lists.map((list, idx) => (
                    <IonButton
                        key={idx}
                        color="danger"
                        size="small"
                        onClick={() => deleteList(idx)}
                    >
                        X
                    </IonButton>
                ))}
            </div>

            {/* KRAV 1: Input field and button for creating new lists */}
            <input
                className="new-list-input"
                value={newListName}
                onChange={e => setNewListName(e.target.value)}
                onKeyPress={handleEnter}
                placeholder="Ny liste (trykk Enter)"
            />
        </>
    );
};

export default ListTabs;
