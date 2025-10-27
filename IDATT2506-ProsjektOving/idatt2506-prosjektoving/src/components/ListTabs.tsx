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

    //Switching between lists, adding lists  and deleting lists
    return (
        <>
            {/* KRAV 3: IonSegment used for tab-based list overview */}
            <IonSegment value={activeIndex.toString()}>
                {lists.map((list, idx) => (
                    <div key={idx} className="list-tab-container">
                        <IonSegmentButton value={idx.toString()} onClick={() => setActiveIndex(idx)}>
                            <IonLabel>{list.name}</IonLabel>
                        </IonSegmentButton>
                        {/* KRAV 2: Delete button for removing lists */}
                        <IonButton
                            color="danger"
                            size="small"
                            className="delete-button"
                            onClick={() => deleteList(idx)}
                        >
                            X
                        </IonButton>
                    </div>
                ))}
            </IonSegment>

            {/* KRAV 1: Input field and button for creating new lists */}
            <input
                className="new-list-input"
                value={newListName}
                onChange={e => setNewListName(e.target.value)}
                placeholder="Ny liste"
            />
            <IonButton onClick={() => { if(newListName) { addList(newListName); setNewListName(''); }}}>Legg til liste</IonButton>
        </>
    );
};

export default ListTabs;
