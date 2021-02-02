import React from 'react';
import { render, screen } from '@testing-library/react';
import App from './App';

test('renders React logo', () => {
  render(<App />);
  const logoElement = screen.getAllByAltText('logo');
  expect(logoElement).toBeInTheDocument();
});
