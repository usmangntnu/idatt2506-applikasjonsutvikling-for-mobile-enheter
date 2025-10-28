import {Filesystem, Directory, Encoding} from '@capacitor/filesystem';
import {ToDoList} from "../models/types";

//KRAV 9: Name of file for saving and retrieving data
const FILE_NAME = 'lists.json';

//krav 9: Saves the provided lists to a file in JSON format
export const saveLists = async (lists: ToDoList[]) => {
    await Filesystem.writeFile({
        path: FILE_NAME,
        data: JSON.stringify(lists),
        directory: Directory.Documents,
        encoding: Encoding.UTF8
    });
};

//krav 9: Function that retrieves the saved lists from the file
export const readLists = async (): Promise<ToDoList[]> => {
    try {
        const contents = await Filesystem.readFile({
            path: FILE_NAME,
            directory: Directory.Documents,
            encoding: Encoding.UTF8
        });
        return JSON.parse(contents.data as string) || [];
    } catch (e) {
        return [];
    }
};


