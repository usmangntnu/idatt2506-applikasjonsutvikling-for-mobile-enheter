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

//Component itself
const ListTabs: React.FC<Props> = ({ lists, activeIndex, setActiveIndex, addList, deleteList }) => {
    const [newListName, setNewListName] = useState('');

    //Switching between lists, adding lists  and deleting lists
    return (
        <>
            <IonSegment value={activeIndex.toString()}>
                {lists.map((list, idx) => (
                    <div key={idx} style={{ display: 'flex', alignItems: 'center' }}>
                        <IonSegmentButton value={idx.toString()} onClick={() => setActiveIndex(idx)}>
                            <IonLabel>{list.name}</IonLabel>
                        </IonSegmentButton>
                        <IonButton
                            color="danger"
                            size="small"
                            onClick={() => deleteList(idx)}
                            style={{ marginLeft: '4px' }}
                        >
                            X
                        </IonButton>
                    </div>
                ))}
            </IonSegment>

            <input value={newListName} onChange={e => setNewListName(e.target.value)} placeholder="Ny liste"/>
            <IonButton onClick={() => { if(newListName) { addList(newListName); setNewListName(''); }}}>Legg til liste</IonButton>
        </>
    );
};

export default ListTabs;
