import React, { Component } from 'react';

interface GuestBookEntry {
  id: string;
  message: string;
}

interface GuestBookProps {
  // do nothing, look pretty
}

interface GuestBookState {
  guestBookEntries: Array<GuestBookEntry>;
  isLoading: boolean;
}

class GuestBook extends Component<GuestBookProps, GuestBookState> {

  constructor(props: GuestBookProps) {
    super(props);

    this.state = {
      guestBookEntries: [],
      isLoading: false
    };
  }

  // TODO move all of the webservice stuff here to another module
  async componentDidMount() {
    this.setState({isLoading: true});

    const response = await fetch('http://localhost:8080/guestbook');
    const data = await response.json();
    this.setState({guestBookEntries: data, isLoading: false});

    const socket = new WebSocket('ws://localhost:8080/ws/guestbook');
    socket.addEventListener('message', async (event: any) => {
      const data = JSON.parse(event.data);
      this.state.guestBookEntries.push(data);
      this.setState({guestBookEntries: this.state.guestBookEntries});
    })
  }

  render() {
    const {guestBookEntries, isLoading} = this.state;

    if (isLoading) {
      return <p>Loading...</p>;
    }
    return (
      <div>
        <h2>Profile List</h2>
        {guestBookEntries.map((entry: GuestBookEntry) =>
          <div key={entry.id}>
            {entry.message}<br/>
          </div>
        )}
        <a href="/" className="App-link">Home</a>
      </div>
    )
  }
}

export default GuestBook;