

import './App.css';
import React, { useEffect, useRef } from 'react';
import Cookies from 'universal-cookie';

const cookies = new Cookies();

function App() {
  const [userName, setUserName] = React.useState('');
  const [password, setPassword] = React.useState('');
  const [isLoading, setIsLoading] = React.useState(false);
  const [isLoggedIn, setIsLoggedIn] = React.useState(false);
  const [errorMessage, setErrorMessage] = React.useState('');

  // new state variables for chat box
  const [toId, setToId] = React.useState('');
  const [message, setMessage] = React.useState('');

  // new state variable for list of convos
  const [conversations, setConversations] = React.useState([]); // default empty array

  const [conversationId, setConversationId] = React.useState('');
  const [thread, setThread] = React.useState([]);

  const [users, setUser] = React.useState([]);
  // Create a ref to the chat container element
  const chatContainerRef = useRef(null);

// if a conversationId, refetch the convo
  React.useEffect(() => {
    // function I want to run
    getSingleThread();
  },[conversationId]); //when these variables change
  
    async function getSingleThread() {
      const httpSettings = {
        method: 'GET',
        headers: {
          auth: cookies.get('auth'), // utility to retrive cookie from cookies
        }
      };
      const result = await fetch(`/getConversation?conversationId=${conversationId}`, httpSettings); //must use back tic, `, same key on tilde (~)
      const apiRes = await result.json();
      console.log(apiRes);
      //todo
      if (apiRes.status) {
        setThread(apiRes.data);
      }
      
    }


  async function getConversations() {
    const httpSettings = {
      method: 'GET',
      headers: {
        auth: cookies.get('auth'), // utility to retrive cookie from cookies
      }
    };
    const result = await fetch('/getConversations', httpSettings);
    const apiRes = await result.json();
    console.log(apiRes);
    if (apiRes.status) {
      // worked
      console.log("data received");//added for delete
      setConversations(apiRes.data); // java side should return list of all convos for this user
    } else {
      setErrorMessage(apiRes.message);
    }
  }

  async function getUsers(user){
    const httpSettings = {
      method: 'GET',
      headers: {
        auth: cookies.get('auth')
      }
    };
    const search = '/getUsers?search=${user}';
    const result = await fetch(search, httpSettings);
    const apiRes = await result.json();
    if (apiRes.status){
      setUser(apiRes.data);
    } else {
      setErrorMessage(apiRes.message);
    }
  }

  function handleInput(e){
    const input = e.target.value;
    setToId(input);
    getUsers(input);
  }

async function handleDeleteConvo(conversationId){//added for delete
  const body = {conversationId: conversationId};
  const httpSettings = { 
    headers: {
      auth: cookies.get('auth') //retrieve cookie from cookies
  },
  body:JSON.stringify(body),
  method: 'POST'
};

  const result = await fetch(`/deleteConversation`, httpSettings);
  const apiRes = await result.json();
  console.log(apiRes); //added foor delete

  if(apiRes.status){
    getConversations(); //worked
  }else {
    setErrorMessage(apiRes.message);
  }
}// added for delete

  async function handleSubmit() {
    setIsLoading(true);
    setErrorMessage(''); // fresh error message each time
    const body = {
      userName: userName,
      password: password,
    };
    const httpSettings = {
      body: JSON.stringify(body),
      method: 'POST'
    };
    const result = await fetch('/createUser', httpSettings);
    const apiRes = await result.json();
    console.log(apiRes);
    if (apiRes.status) {
      // user was created
      // todo
    } else {
      // some error message
      setErrorMessage(apiRes.message);
    }
    setIsLoading(false);
  };

  async function handleBlock(){
    setErrorMessage('');
  
        const body1 = {
        fromId: userName,
        toId : toId,
        }
  
        const httpSettings1 = {
        headers: {
          auth: cookies.get('auth') //retrieve cookie from cookies
        },
        body: JSON.stringify(body1),
        method:'POST'
        };
  
        const result1 = await fetch('/blocked', httpSettings1)
        const apiRes = await result1.json();
        setErrorMessage(apiRes.message);
  
    };

    async function handleLogIn() {
      setIsLoading(true);
      setErrorMessage(''); // fresh error message each time
      const body = {
        userName: userName,
        password: password,
      };
      const httpSettings = {
        body: JSON.stringify(body),
        method: 'POST'
      };
      const result = await fetch('/login', httpSettings);
      if (result.status === 200) {
        // login worked
        setIsLoggedIn(true);
        getConversations();
        setConversationId(''); // Reset conversationId when logged in
        // Add the following line to fetch the conversation thread
        getSingleThread();
      } else {
        // login did not work
        setErrorMessage(`Username or password incorrect.`);
      }
      setIsLoading(false);
    }
    

    async function handleSendMessage() {
      setIsLoading(true);
      setErrorMessage(''); // fresh error message each time
      const body = {
        fromId: userName,
        toId: toId,
        message: message,
        time: new Date(Date.now()).getHours() + ":" + new Date(Date.now()).getMinutes()
      };
      const httpSettings = {
        body: JSON.stringify(body),
        method: 'POST',
        headers: {
          auth: cookies.get('auth'),
        }
      };
      const result = await fetch('/createMessage', httpSettings);
      const apiRes = await result.json();
      console.log(apiRes);
      if (apiRes.status) {
        // worked
        setMessage('');
        getConversations();
        // getSingleThread();
        // setConversationId(apiRes.data.conversationId); // Update conversationId here
      } else {
        setErrorMessage(apiRes.message);
      }
      setIsLoading(false);
    }

    async function handleRandom() {
      setIsLoading(true);
      setErrorMessage('');
    
      const httpSettings = {
        method: 'POST',
        headers: {
          auth: cookies.get('auth')
        }
      };
    
      const result = await fetch('/handleRandom', httpSettings);
      const apiRes = await result.json();
      const randomIndex = Math.floor(Math.random() * users.length);
      const randomUser = users[randomIndex];
      console.log(apiRes);
      if (apiRes.status) {
        setToId(randomUser.userName); // Set the random user ID as the "toId"
        // setToId(apiRes.data.randomUser.userName); 
      } else {
        setErrorMessage(apiRes.message);
      }
    
      setIsLoading(false);
    }


  if (isLoggedIn) {
    
    return (
      <div className="App">
        <div className="chat-window">
          <div className="chat-header">
            <h1>Welcome {userName}</h1>
            <div>
              To: <input value={toId} onClick={handleInput} onChange={handleInput}  />
              <div>
              <div>
              <button className ="block" onClick={handleBlock}>Block {toId}</button>
              </div>
                <div className='userSearch'>
                  {users.filter(user => user.userName.includes(toId)).map(user => <div>user: {user.userName}</div>)}
                </div>
              <p>Talking to: {conversationId}</p>

              </div>
            </div>
            <div className="chat-body">
              <div>{errorMessage}</div>
              {/* <div>{conversations.map(conversation => <div ></div>)}</div> */}

              <div className="chat-container" ref={chatContainerRef}>
                {thread.map((message, index) => (
                  <h5 key={index} className={message.fromId === userName
                    ? 'chat-message-from' : 'chat-message-to'}> {message.message}</h5>
                ))}
              </div>

            </div>
            <div className="chat-box">
              <input
                value={message}
                type="text"
                placeholder="Type Message..."
                onChange={e => setMessage(e.target.value)}
                />
              <button onClick={handleSendMessage}>Send Message</button>
            </div>
              <button onClick={handleBlock}>Block</button>
              <button onClick={handleRandom}>Select Random User</button>
          </div>
        </div>
          <div className="delete-convo">{ //added for delete
            conversations.map((conversation, index) => (
            <div key={index} onClick={() => setConversationId(conversation.conversationId)}>
              Convo: {conversation.conversationId}
              <button onClick={() => handleDeleteConvo(conversation.conversationId)}>Delete Convo</button>
            </div>))
              /*added for delete */}
          </div>
      </div>
      
    );
  }

 

  return (
    <div className="login-reg">
        <div className="login-user">
          <input value={userName} onChange={e => setUserName(e.target.value)} />
          <input value={password} onChange={e => setPassword(e.target.value)} type="password" />
          <button onClick={handleSubmit} disabled={isLoading}>Register</button>
          <button onClick={handleLogIn} disabled={isLoading}>Log in</button>
        </div>
        <div>
          {isLoading ? 'Loading ...' : null}
        </div>
        <div>{errorMessage}</div>
    </div>
  );
}

export default App;