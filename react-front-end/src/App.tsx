import React, {Component} from 'react';
import logo from './logo.svg';
import './App.css';
import GuestBook from "./GuestBook";

class App extends Component {
  render() {
    return (
      <div className="App">
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo"/>
          <GuestBook/>
        </header>
      </div>
    );
  }
}

export default App;
