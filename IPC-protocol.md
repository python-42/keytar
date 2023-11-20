# Keytar IPC Protocol
This file specifies how the keytar IPC communicates with GUIs.

## Connection
Connect to the IPC via a socket. Domain should be localhost. Port may be configured using the `keytar.ipc.port` Java property. Ensure that the server socket is open and listening for connections before launching keytar. 

## Output
The keytar IPC outputs JSON strings. The strings are of two types.

### Event
This type of output indicates that an event has occured, but there is not any data associated with the event. It has only one attribute: 
1. name - the name of the event. 
Events which currently are in use include:

 |Name | Trigger Condition |
 |--- | ---- |
|`end` |Occurs when the end button on the controller is pressed, stopping execution.|
|`char_sent`| Occurs when currently selected character is sent with the strum bar |
|`char_discard` | Occurs when currently selected character is discarded with the strum bar |


### Data
The other type of output is a data output. This is similar to an event in that it occurs when an event occurs within the keytar code. However, these events also require the transmission of relevant data. This output has two attributes: 
1. name - the name of the event
2. data - data associated with the event

Data events which are currently in use are: 

 Name | Trigger Condition | Data
| --- | ----------- | ------|
|`startup` | Occurs when the event loop starts. | Keyset (`char[][][]`)
| `caps` | Caps lock is toggled | Current value of caps (`boolean`) | 
| `keymap` | Keymap is switched | Currently selected keymap (`int`) |
|`row_sel` |A row is selected using fret key|Currently selected rows (`int`)| 
|`col_sel`| A column is selected using fret key (occurs after `row_sel`)|Currently selected column (`int`)|
|`arrow_sent`| Arrow key is sent using strum bar | 'U', 'D', 'L', or 'R' identifying sent arrow key (`char`)
 



## Input
No input is expected. Any input will be ignored.