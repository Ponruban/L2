// MemberSelector Component
import React from 'react';
import { Select } from '@/components/ui';

interface MemberSelectorProps {
  users: Array<{ id: number; name: string }>;
  selectedIds: number[];
  onSelectionChange: (selectedIds: number[]) => void;
}

const MemberSelector: React.FC<MemberSelectorProps> = ({
  users,
  selectedIds,
  onSelectionChange,
}) => {
  const options = users.map(user => ({
    value: user.id,
    label: user.name,
  }));

  return (
    <Select
      label="Team Members"
      options={options}
      value={selectedIds}
      onChange={(value) => onSelectionChange(Array.isArray(value) ? value.map(v => Number(v)) : [Number(value)])}
      multiple
      fullWidth
    />
  );
};

export default MemberSelector; 