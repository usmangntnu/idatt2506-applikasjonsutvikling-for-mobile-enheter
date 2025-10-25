import {Filesystem, Directory, Encoding} from '@capacitor/filesystem';
import {ToDoList} from "../models/types";

//Name of file for saving and retrieving data
const FILE_NAME = 'lists.json';

//Saves the provided lists to a file in JSON format
export const saveLists = async (lists: TodoList[]) => {
    await Filesystem.writeFile({
        path: FILE_NAME,
        data: JSON.stringify(lists),
        directory: Directory.Data,
        encoding: Encoding.UTF8
    });
};

